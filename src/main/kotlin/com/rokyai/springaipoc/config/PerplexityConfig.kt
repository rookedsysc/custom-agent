package com.rokyai.springaipoc.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PerplexityConfig {

    @Value("\${app.perplexity.api-key}")
    private lateinit var apiKey: String

    @Value("\${app.perplexity.base-url}")
    private lateinit var baseUrl: String

    @Value("\${app.perplexity.chat.model}")
    private lateinit var model: String

    @Bean(name = ["perplexityChatModel"])
    fun perplexityChatModel(): ChatModel {
        val openAiApi = OpenAiApi.builder()
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .completionsPath("/chat/completions")
            .build()

        val options = OpenAiChatOptions.builder()
            .model(model)
            .temperature(0.7)
            .build()

        return OpenAiChatModel.builder()
            .openAiApi(openAiApi)
            .defaultOptions(options)
            .build()
    }

    @Bean(name = ["perplexityChatClient"])
    fun perplexityChatClient(): ChatClient {
        return ChatClient.create(perplexityChatModel())
    }
}
