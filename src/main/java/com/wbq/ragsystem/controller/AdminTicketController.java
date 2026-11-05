package com.wbq.ragsystem.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wbq.ragsystem.ticket.HelpCenterTicket;
import com.wbq.ragsystem.ticket.HelpCenterTicketService;
import com.wbq.ragsystem.ticket.TicketCategory;
import com.wbq.ragsystem.ticket.TicketNotFoundException;
import com.wbq.ragsystem.ticket.UpdateTicketRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/help_center/admin/tickets")
@RequiredArgsConstructor
@Slf4j
public class AdminTicketController {

    private final HelpCenterTicketService ticketService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        log.debug("admin list tickets page={} size={} status={} keyword={}", page, size, status, keyword);
        Page<HelpCenterTicket> result = ticketService.list(page, size, status, keyword);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("content", result.getContent().stream().map(this::toSummary).toList());
        body.put("page", result.getNumber());
        body.put("size", result.getSize());
        body.put("totalElements", result.getTotalElements());
        body.put("totalPages", result.getTotalPages());
        return body;
    }

    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        log.info("admin ticket detail id={}", id);
        return toDetail(ticketService.get(id));
    }

    @PatchMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody UpdateTicketRequest body) {
        log.info("admin ticket update id={} status={}", id, body.status());
        return toDetail(ticketService.update(id, body));
    }

    @ExceptionHandler(TicketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(TicketNotFoundException e) {
        log.warn("admin ticket not found: {}", e.getMessage());
        return Map.of("message", e.getMessage());
    }

    private Map<String, Object> toSummary(HelpCenterTicket t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", t.getId());
        m.put("ticketNo", t.getTicketNo());
        m.put("userId", t.getUserId());
        m.put("userName", t.getUserName());
        m.put("category", t.getCategory());
        m.put("categoryLabel", TicketCategory.labelOf(t.getCategory()));
        m.put("contact", t.getContact());
        m.put("description", t.getDescription());
        m.put("status", t.getStatus());
        m.put("createdAt", t.getCreatedAt());
        return m;
    }

    private Map<String, Object> toDetail(HelpCenterTicket t) {
        Map<String, Object> m = toSummary(t);
        m.put("chatId", t.getChatId());
        m.put("internalNote", t.getInternalNote());
        m.put("reply", t.getReply());
        m.put("product", t.getProduct());
        m.put("locale", t.getLocale());
        m.put("updatedAt", t.getUpdatedAt());
        m.put("chatTranscript", parseTranscript(t.getChatTranscript()));
        m.put("attachments", t.getAttachments() != null ? t.getAttachments() : List.of());
        return m;
    }

    private List<Map<String, Object>> parseTranscript(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.warn("parse chatTranscript failed", e);
            return List.of();
        }
    }
}
