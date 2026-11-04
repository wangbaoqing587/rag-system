package com.wbq.ragsystem.ticket;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(Long id) {
        super("ticket not found: " + id);
    }
}
