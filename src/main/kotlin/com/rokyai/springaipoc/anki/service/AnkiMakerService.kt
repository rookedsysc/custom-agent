package com.rokyai.springaipoc.anki.service

import com.rokyai.springaipoc.anki.constants.ANKI_PROMPT
import com.rokyai.springaipoc.anki.constants.SEARCH_PROMPT
import com.rokyai.springaipoc.anki.dto.SearchAnkiResponse
import com.rokyai.springaipoc.chat.dto.ChatRequest
import com.rokyai.springaipoc.chat.dto.ChatResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class AnkiMakerService(
    chatClientBuilder: ChatClient.Builder,
    private val mcpToolProvider: ToolCallbackProvider,
    @Qualifier("perplexityChatClient") private val perplexityChatClient: ChatClient
) {
    private val chatClient: ChatClient = chatClientBuilder.build()

    suspend fun searchAndGenerateAnki(request: ChatRequest): SearchAnkiResponse = coroutineScope {
        val searchDeferred = async { search(request) }
        val ankiDeferred = async { generateAnkiContent(request) }

        val searchResponse = searchDeferred.await()
        val ankiResponse = ankiDeferred.await()

        SearchAnkiResponse(
            searchContent = searchResponse.message,
            ankiContent = ankiResponse.message
        )
    }

    private suspend fun generateAnkiContent(request: ChatRequest): ChatResponse {
        val ankiContent = chatClient.prompt()
            .system(ANKI_PROMPT)
            .user(request.message)
            .toolCallbacks(mcpToolProvider)
            .call()
            .content()
            ?: throw IllegalStateException("ChatGPT 응답 생성 실패")

        return ChatResponse(message = ankiContent)
    }

    private suspend fun search(request: ChatRequest): ChatResponse {
        val searchContent = perplexityChatClient.prompt()
            .system(SEARCH_PROMPT)
            .user(request.message)
            .toolCallbacks(mcpToolProvider)
            .call()
            .content()
            ?: throw IllegalStateException("ChatGPT 응답 생성 실패")

        return ChatResponse(message = searchContent)
    }
}
