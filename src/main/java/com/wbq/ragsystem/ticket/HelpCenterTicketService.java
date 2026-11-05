package com.wbq.ragsystem.ticket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HelpCenterTicketService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZONE);

    private final HelpCenterTicketRepository repository;
    private final ObjectMapper objectMapper;

    private static final int MAX_ATTACHMENTS = 4;
    private static final int MAX_ATTACHMENT_CHARS = 1_500_000;

    @Transactional
    public Map<String, Object> create(CreateTicketRequest req) {
        if (!TicketCategory.isValid(req.category())) {
            throw new IllegalArgumentException("category invalid");
        }
        if (req.description() == null || req.description().isBlank()) {
            throw new IllegalArgumentException("description required");
        }
        if (req.contact() == null || req.contact().isBlank()) {
            throw new IllegalArgumentException("contact required");
        }
        List<String> attachments = normalizeAttachments(req.attachments());

        HelpCenterTicket ticket = new HelpCenterTicket();
        ticket.setTicketNo(nextTicketNo());
        ticket.setUserId(req.userId());
        ticket.setUserName(req.userName());
        ticket.setChatId(req.chatId());
        ticket.setCategory(req.category());
        ticket.setDescription(req.description().trim());
        ticket.setContact(req.contact().trim());
        ticket.setProduct(req.product());
        ticket.setLocale(req.locale());
        ticket.setStatus(TicketStatus.PENDING.name());
        try {
            ticket.setChatTranscript(objectMapper.writeValueAsString(req.chatTranscript()));
            ticket.setAttachments(attachments);
        } catch (JsonProcessingException e) {
            log.warn("create ticket invalid payload userId={}", req.userId(), e);
            throw new IllegalArgumentException("ticket payload invalid");
        }
        HelpCenterTicket saved = repository.save(ticket);
        log.info("ticket created ticketNo={} id={} userId={} attachmentCount={}",
                saved.getTicketNo(), saved.getId(), saved.getUserId(),
                attachments.size());
        return toCreateResponse(saved);
    }

    public Page<HelpCenterTicket> list(int page, int size, String status, String keyword) {
        return repository.search(emptyToNull(status), emptyToNull(keyword), PageRequest.of(page, size));
    }

    public HelpCenterTicket get(Long id) {
        return repository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
    }

    @Transactional
    public HelpCenterTicket update(Long id, UpdateTicketRequest req) {
        HelpCenterTicket ticket = get(id);
        if (req.status() != null && !req.status().isBlank()) {
            ticket.setStatus(req.status());
        }
        if (req.internalNote() != null) {
            ticket.setInternalNote(req.internalNote());
        }
        if (req.reply() != null) {
            ticket.setReply(req.reply());
        }
        HelpCenterTicket saved = repository.save(ticket);
        log.info("ticket updated id={} ticketNo={} status={}", saved.getId(), saved.getTicketNo(), saved.getStatus());
        return saved;
    }

    private String nextTicketNo() {
        String day = DAY.format(Instant.now());
        String prefix = "HC" + day;
        long count = repository.countByTicketNoStartingWith(prefix);
        return prefix + String.format("%04d", count + 1);
    }

    private Map<String, Object> toCreateResponse(HelpCenterTicket t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", t.getId());
        m.put("ticketNo", t.getTicketNo());
        m.put("status", t.getStatus());
        m.put("createdAt", t.getCreatedAt());
        return m;
    }

    private String emptyToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }

    private List<String> normalizeAttachments(List<String> attachments) {
        if (attachments == null || attachments.isEmpty()) {
            return List.of();
        }
        if (attachments.size() > MAX_ATTACHMENTS) {
            throw new IllegalArgumentException("attachments exceed limit");
        }
        for (String item : attachments) {
            if (item == null || item.isBlank()) {
                throw new IllegalArgumentException("attachment invalid");
            }
            if (item.length() > MAX_ATTACHMENT_CHARS) {
                throw new IllegalArgumentException("attachment too large");
            }
        }
        return List.copyOf(attachments);
    }
}
