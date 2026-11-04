package com.wbq.ragsystem.ticket;

import java.util.List;

public record CreateTicketRequest(
        String userId,
        String userName,
        String chatId,
        String description,
        List<TranscriptItem> chatTranscript,
        String product,
        String locale,
        String contact) {
}
