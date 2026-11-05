package com.wbq.ragsystem.controller;

import com.wbq.ragsystem.admin.HelpCenterAdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/help_center/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final HelpCenterAdminAuthService authService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginBody body) {
        return authService.login(body.username(), body.password())
                .map(token -> Map.of("token", token))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误"));
    }

    public record LoginBody(String username, String password) {
    }
}
