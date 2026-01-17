package com.rokyai.springaipoc.anki.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Gemini Deep Research API 초기 요청 DTO
 *
 * @property input 연구 질문 또는 주제
 * @property agent 사용할 에이전트 모델 (기본값: deep-research-pro-preview-12-2025)
 * @property background 비동기 실행 여부 (Deep Research는 필수로 true)
 * @property stream 실시간 스트리밍 여부 (선택)
 */
data class GeminiDeepResearchRequest(
    @JsonProperty("input")
    val input: String,

    @JsonProperty("agent")
    val agent: String = "deep-research-pro-preview-12-2025",

    @JsonProperty("background")
    val background: Boolean = true,

    @JsonProperty("stream")
    val stream: Boolean = false
)
