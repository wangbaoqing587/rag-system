package com.wbq.ragsystem.ticket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Converter
public class TicketReplyListJsonConverter implements AttributeConverter<List<TicketReply>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    public String convertToDatabaseColumn(List<TicketReply> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Override
    public List<TicketReply> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return new ArrayList<>();
        }
        String trimmed = dbData.trim();
        if (!trimmed.startsWith("[")) {
            List<TicketReply> legacy = new ArrayList<>();
            legacy.add(new TicketReply(trimmed, null));
            return legacy;
        }
        try {
            List<TicketReply> list = MAPPER.readValue(trimmed, new TypeReference<>() {
            });
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (Exception ignored) {
            return new ArrayList<>();
        }
    }
}
