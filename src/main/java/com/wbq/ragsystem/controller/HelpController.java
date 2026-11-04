package com.wbq.ragsystem.controller;

import com.wbq.ragsystem.app.HelpCenterApp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/ai/help_center")
@Slf4j
public class HelpController {

    @Resource
    private HelpCenterApp helpCenterApp;

    @GetMapping("/chat/sync")
    public String chatSync(String message, String chatId) {
        log.info("help_center chat sync chatId={}", chatId);
        return helpCenterApp.doChatWithRag(message, chatId);
    }

    @GetMapping("/chat/sse")
    public SseEmitter chatSse(String message, String chatId) {
        log.info("help_center chat sse chatId={}", chatId);
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter emitter = new SseEmitter(180000L); // 3分钟超时
        // 获取 Flux 数据流并直接订阅
        helpCenterApp.doChatByStream(message, chatId)
                .subscribe(
                        // 处理每条消息
                        chunk -> {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                log.warn("help_center chat sse send chunk failed chatId={}", chatId, e);
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理错误
                        emitter::completeWithError,
                        // 处理完成
                        emitter::complete
                );
        // 返回emitter
        return emitter;
    }
}
