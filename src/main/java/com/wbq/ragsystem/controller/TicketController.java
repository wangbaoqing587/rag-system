package com.wbq.ragsystem.controller;

import com.wbq.ragsystem.ticket.CreateTicketRequest;
import com.wbq.ragsystem.ticket.HelpCenterTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/help_center/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final HelpCenterTicketService ticketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> create(@RequestBody CreateTicketRequest body) {
        log.info("create ticket userId={} chatId={}", body.userId(), body.chatId());
        return ticketService.create(body);
    }
}
