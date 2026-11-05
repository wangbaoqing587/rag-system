package com.wbq.ragsystem.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HelpCenterAdminAuthService {

    private static final Duration SESSION_TTL = Duration.ofHours(24);

    @Value("${help-center.admin.username:admin}")
    private String username;

    @Value("${help-center.admin.password:admin}")
    private String password;

    private final ConcurrentHashMap<String, Instant> sessions = new ConcurrentHashMap<>();

    public Optional<String> login(String user, String pass) {
        if (!username.equals(user) || !password.equals(pass)) {
            return Optional.empty();
        }
        String token = UUID.randomUUID().toString();
        sessions.put(token, Instant.now().plus(SESSION_TTL));
        return Optional.of(token);
    }

    public boolean validate(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Instant expiresAt = sessions.get(token);
        if (expiresAt == null) {
            return false;
        }
        if (Instant.now().isAfter(expiresAt)) {
            sessions.remove(token);
            return false;
        }
        return true;
    }

    public void logout(String token) {
        if (token != null) {
            sessions.remove(token);
        }
    }
}
