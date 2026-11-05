package com.wbq.ragsystem.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

public class HelpCenterQueryAugmenter {
    public static ContextualQueryAugmenter createInstance() {
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                未检索到任何帮助文档。你必须只输出下面这一行，不要任何其他文字、标点、解释或空行：
                [TICKET_OFFER]
                """);
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();
    }
}
