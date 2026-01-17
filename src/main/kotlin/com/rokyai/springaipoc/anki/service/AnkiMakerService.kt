package com.rokyai.springaipoc.anki.service

import com.rokyai.springaipoc.anki.constants.ANKI_PROMPT
import com.rokyai.springaipoc.anki.dto.GeminiDeepResearchRequest
import com.rokyai.springaipoc.anki.dto.GeminiInteractionResponse
import com.rokyai.springaipoc.anki.dto.SearchAnkiResponse
import com.rokyai.springaipoc.chat.dto.ChatRequest
import com.rokyai.springaipoc.chat.dto.ChatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Service
class AnkiMakerService(
    @Qualifier("openAiChatClient") private val openAiChatModel: ChatClient,
    @Qualifier("geminiDeepResearchRestClient") private val geminiDeepResearchRestClient: RestClient
) {

    suspend fun searchAndGenerateAnki(request: ChatRequest): SearchAnkiResponse = coroutineScope {
        val searchResponse = search(request)
        val ankiResponse = generateAnkiContent(request, searchResponse.message)

        SearchAnkiResponse(
            searchContent = searchResponse.message,
            ankiContent = ankiResponse.message
        )
    }

    private suspend fun generateAnkiContent(
        request: ChatRequest,
        searchContext: String
    ): ChatResponse = withContext(Dispatchers.IO) {
        val userPrompt = """
            # 검색 결과
            $searchContext

            # 사용자 질문
            ${request.message}

            위 검색 결과를 참고하여 사용자 질문에 대한 Anki 카드를 생성해주세요.
        """.trimIndent()

        val ankiContent = openAiChatModel.prompt()
            .system(ANKI_PROMPT)
            .user(userPrompt)
            .call()
            .content()
            ?: throw IllegalStateException("ChatGPT 응답 생성 실패")

        ChatResponse(message = ankiContent)
    }

    /**
     * Gemini Deep Research API를 사용하여 심층 검색을 수행합니다.
     *
     * Deep Research는 다단계 연구 작업을 자율적으로 계획하고 실행하여
     * 인용을 포함한 상세 보고서를 생성합니다.
     *
     * 작업 흐름:
     * 1. 비동기 연구 작업 시작 (interaction ID 획득)
     * 2. 폴링을 통한 작업 상태 확인 (10초 간격)
     * 3. 완료 시 생성된 보고서 반환
     *
     * @param request 사용자 검색 요청
     * @return 생성된 연구 보고서를 포함하는 ChatResponse
     * @throws IllegalStateException API 호출 실패 또는 타임아웃시
     */
    private suspend fun search(request: ChatRequest): ChatResponse = withContext(Dispatchers.IO) {
        val searchRequest = GeminiDeepResearchRequest(
            input = request.message
        )

        val initialResponse = geminiDeepResearchRestClient
            .post()
            .uri("/v1beta/interactions")
            .body(searchRequest)
            .retrieve()
            .body<GeminiInteractionResponse>()
            ?: throw IllegalStateException("Gemini Deep Research API 초기 요청 실패")

        val interactionId = initialResponse.id
        val researchResult = pollForCompletion(interactionId)

        ChatResponse(message = researchResult)
    }

    /**
     * Gemini Deep Research 작업 완료를 폴링으로 확인합니다.
     *
     * 최대 60분(3600초) 동안 10초 간격으로 상태를 확인합니다.
     *
     * @param interactionId 조회할 Interaction ID
     * @return 완료된 연구 보고서 텍스트
     * @throws IllegalStateException 작업 실패 또는 타임아웃시
     */
    private suspend fun pollForCompletion(interactionId: String): String = withContext(Dispatchers.IO) {
        val maxAttempts = 360  // 60분 (10초 * 360회)
        var attempts = 0

        while (attempts < maxAttempts) {
            val statusResponse = geminiDeepResearchRestClient
                .get()
                .uri("/v1beta/interactions/$interactionId")
                .retrieve()
                .body<GeminiInteractionResponse>()
                ?: throw IllegalStateException("Gemini 상태 조회 실패")

            when (statusResponse.status) {
                "completed" -> {
                    return@withContext statusResponse.outputs?.firstOrNull()?.text
                        ?: throw IllegalStateException("연구 결과가 비어있습니다")
                }
                "failed" -> {
                    throw IllegalStateException(
                        "Gemini Deep Research 실패: ${statusResponse.error?.message ?: "알 수 없는 오류"}"
                    )
                }
                "in_progress" -> {
                    delay(10_000)  // 10초 대기
                    attempts++
                }
                else -> {
                    throw IllegalStateException("알 수 없는 상태: ${statusResponse.status}")
                }
            }
        }

        throw IllegalStateException("Gemini Deep Research 타임아웃 (60분 초과)")
    }
}
