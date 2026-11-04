package com.wbq.ragsystem.ticket;

public record UpdateTicketRequest(String status, String internalNote, String reply) {
}
