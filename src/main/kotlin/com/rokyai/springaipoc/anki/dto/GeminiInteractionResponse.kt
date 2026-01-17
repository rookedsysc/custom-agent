package com.rokyai.springaipoc.anki.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Gemini Deep Research 초기 응답 DTO
 *
 * @property id Interaction ID (폴링 조회에 사용)
 * @property status 작업 상태 (in_progress, completed, failed)
 * @property outputs 완료 시 생성된 결과 리스트
 * @property error 실패 시 에러 정보
 */
data class GeminiInteractionResponse(
    @JsonProperty("id")
    val id: String,

    @JsonProperty("status")
    val status: String,

    @JsonProperty("outputs")
    val outputs: List<GeminiOutput>? = null,

    @JsonProperty("error")
    val error: GeminiError? = null
)

/**
 * Gemini Deep Research 출력 결과
 *
 * @property text 생성된 보고서 텍스트
 */
data class GeminiOutput(
    @JsonProperty("text")
    val text: String
)

/**
 * Gemini API 에러 정보
 *
 * @property message 에러 메시지
 * @property code 에러 코드
 */
data class GeminiError(
    @JsonProperty("message")
    val message: String,

    @JsonProperty("code")
    val code: String? = null
)
