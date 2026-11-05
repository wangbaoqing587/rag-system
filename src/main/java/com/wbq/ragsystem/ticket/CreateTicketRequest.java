package com.wbq.ragsystem.ticket;

import java.util.List;

public record CreateTicketRequest(
        String userId,
        String userName,
        String chatId,
        String category,
        String description,
        String contact,
        List<String> attachments,
        List<TranscriptItem> chatTranscript,
        String product,
        String locale) {
}
