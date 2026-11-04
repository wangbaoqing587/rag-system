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
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HelpCenterTicketService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZONE);

    private final HelpCenterTicketRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Map<String, Object> create(CreateTicketRequest req) {
        HelpCenterTicket ticket = new HelpCenterTicket();
        ticket.setTicketNo(nextTicketNo());
        ticket.setUserId(req.userId());
        ticket.setUserName(req.userName());
        ticket.setChatId(req.chatId());
        ticket.setDescription(req.description());
        ticket.setProduct(req.product());
        ticket.setLocale(req.locale());
        ticket.setStatus(TicketStatus.PENDING.name());
        try {
            ticket.setChatTranscript(objectMapper.writeValueAsString(req.chatTranscript()));
        } catch (JsonProcessingException e) {
            log.warn("create ticket invalid chatTranscript userId={}", req.userId(), e);
            throw new IllegalArgumentException("chatTranscript invalid");
        }
        HelpCenterTicket saved = repository.save(ticket);
        log.info("ticket created ticketNo={} id={} userId={}", saved.getTicketNo(), saved.getId(), saved.getUserId());
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
}
