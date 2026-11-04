package com.wbq.ragsystem.app;

import com.wbq.ragsystem.chatmemory.FileBasedChatMemory;
import com.wbq.ragsystem.rag.QueryRewriter;
import com.wbq.ragsystem.rag.RagCustomAdvisorFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class HelpCenterApp {

    private final ChatClient chatClient;

    @Resource
    private VectorStore vectorStore;

    @Resource
    private QueryRewriter queryRewriter;

    private static final String SYSTEM_PROMPT = """
            你是产品帮助中心智能助手。根据检索到的帮助文档回答用户问题。
            若文档中有匹配内容，回答开头使用格式：根据帮助说明（「{文档标题}」）：
            然后给出清晰步骤化正文。仅依据文档内容，不要编造。
            """;

    public HelpCenterApp(ChatModel dashscopeChatModel) {
        String fileDir = System.getProperty("user.dir") + "/help-center-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public String doChatWithRag(String message, String chatId) {
        log.info("doChatWithRag chatId={}", chatId);
        // 查询重写
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                // 使用改写后的查询
                .user(rewrittenMessage)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // 开启日志，便于观察效果
                .advisors(new SimpleLoggerAdvisor())
                // 应用知识库问答（Spring AI 内置「向量库问答」Advisor）
//               .advisors(QuestionAnswerAdvisor.builder(loveAppVectorStore).build())
                // 应用知识库问答（自定义「向量库问答」Advisor）
                .advisors(
                        RagCustomAdvisorFactory.createRagAdvisor(
                                vectorStore
                        )
                )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    public Flux<String> doChatByStream(String message, String chatId) {
        log.info("doChatByStream chatId={}", chatId);
        // 查询重写
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        return chatClient
                .prompt()
                .user(rewrittenMessage)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId))
                // 开启日志，便于观察效果
                .advisors(new SimpleLoggerAdvisor())
                // 应用知识库问答（自定义「向量库问答」Advisor）
                .advisors(
                        RagCustomAdvisorFactory.createRagAdvisor(
                                vectorStore
                        )
                )
                .stream()
                .content();
    }
}
