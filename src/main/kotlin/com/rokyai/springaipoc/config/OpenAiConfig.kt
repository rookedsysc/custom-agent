package com.rokyai.springaipoc.config

import org.springframework.context.annotation.Configuration

/**
 * OpenAI 설정
 *
 * Spring AI의 OpenAI Auto-Configuration을 사용합니다.
 * application.yml의 spring.ai.openai.* 설정을 통해 자동으로 구성됩니다.
 *
 * 설정 예시:
 * - spring.ai.openai.api-key: API 키
 * - spring.ai.openai.chat.options.model: 사용할 모델 (gpt-4o-mini 등)
 * - spring.ai.openai.chat.options.temperature: 응답의 창의성 수준
 */
@Configuration
class OpenAiConfig
