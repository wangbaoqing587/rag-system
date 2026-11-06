package com.wbq.ragsystem.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "help_center_ticket")
public class HelpCenterTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_no", nullable = false, unique = true, length = 32)
    private String ticketNo;

    @Column(name = "user_id", nullable = false, length = 16)
    private String userId;

    @Column(name = "user_name", nullable = false, length = 12)
    private String userName;

    @Column(name = "chat_id", nullable = false, length = 64)
    private String chatId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(length = 32)
    private String category;

    @Column(length = 64)
    private String contact;

    @Convert(converter = StringListJsonConverter.class)
    @Column(columnDefinition = "json")
    private List<String> attachments;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "chat_transcript", columnDefinition = "json")
    private String chatTranscript;

    @Column(length = 32)
    private String product;

    @Column(length = 16)
    private String locale;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "internal_note", columnDefinition = "TEXT")
    private String internalNote;

    @Convert(converter = TicketReplyListJsonConverter.class)
    @Column(name = "reply", columnDefinition = "json")
    private List<TicketReply> replies;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (status == null) {
            status = TicketStatus.PENDING.name();
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
