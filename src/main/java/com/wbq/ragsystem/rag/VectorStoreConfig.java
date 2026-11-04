package com.wbq.ragsystem.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class VectorStoreConfig {

    @Resource
    private DocumentLoader myDocumentLoader;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Resource
    private TokenTextSplitter myTokenTextSplitter;

    @Bean
    VectorStore vectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        List<Document> documents = new ArrayList<>(myDocumentLoader.loadMarkdowns());
        List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(documents);
        List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(enrichedDocuments);
        simpleVectorStore.add(splitDocuments);
        log.info("vector store ready rawDocs={} enriched={} chunks={}", documents.size(),
                enrichedDocuments.size(), splitDocuments.size());
        return simpleVectorStore;
    }
}
