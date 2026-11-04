package com.wbq.ragsystem.chatmemory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileBasedChatMemory implements ChatMemory {

    private final Path baseDir;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FileBasedChatMemory(String dir) {
        this.baseDir = Path.of(dir);
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            log.error("create chat memory dir failed dir={}", dir, e);
            throw new IllegalStateException("Failed to create chat memory directory: " + dir, e);
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMessages = new ArrayList<>(get(conversationId));
        conversationMessages.addAll(messages);
        saveConversation(conversationId, conversationMessages);
    }

    @Override
    public List<Message> get(String conversationId) {
        Path file = getConversationFile(conversationId);
        if (!Files.exists(file)) {
            return new ArrayList<>();
        }
        try {
            List<StoredMessage> stored = objectMapper.readValue(file.toFile(), new TypeReference<>() {
            });
            List<Message> messages = new ArrayList<>();
            for (StoredMessage item : stored) {
                messages.add(toMessage(item));
            }
            return messages;
        } catch (IOException e) {
            log.error("read conversation failed conversationId={}", conversationId, e);
            throw new IllegalStateException("Failed to read conversation: " + conversationId, e);
        }
    }

    @Override
    public void clear(String conversationId) {
        try {
            Files.deleteIfExists(getConversationFile(conversationId));
        } catch (IOException e) {
            log.error("clear conversation failed conversationId={}", conversationId, e);
            throw new IllegalStateException("Failed to clear conversation: " + conversationId, e);
        }
    }

    private void saveConversation(String conversationId, List<Message> messages) {
        List<StoredMessage> stored = new ArrayList<>();
        for (Message message : messages) {
            stored.add(fromMessage(message));
        }
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(getConversationFile(conversationId).toFile(), stored);
        } catch (IOException e) {
            log.error("save conversation failed conversationId={}", conversationId, e);
            throw new IllegalStateException("Failed to save conversation: " + conversationId, e);
        }
    }

    private Path getConversationFile(String conversationId) {
        return baseDir.resolve(conversationId + ".json");
    }

    private static Message toMessage(StoredMessage stored) {
        MessageType type = MessageType.fromValue(stored.messageType());
        return switch (type) {
            case USER -> new UserMessage(stored.text());
            case ASSISTANT -> new AssistantMessage(stored.text());
            case SYSTEM -> new SystemMessage(stored.text());
            default -> throw new IllegalArgumentException("Unsupported message type: " + type);
        };
    }

    private static StoredMessage fromMessage(Message message) {
        return new StoredMessage(message.getMessageType().getValue(), message.getText());
    }

    private record StoredMessage(String messageType, String text) {
    }
}
