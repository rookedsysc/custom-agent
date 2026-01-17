package com.rokyai.springaipoc.anki.service

import com.rokyai.springaipoc.anki.constants.ANKI_PROMPT
import com.rokyai.springaipoc.anki.dto.PerplexitySearchRequest
import com.rokyai.springaipoc.anki.dto.PerplexitySearchResponse
import com.rokyai.springaipoc.anki.dto.SearchAnkiResponse
import com.rokyai.springaipoc.chat.dto.ChatRequest
import com.rokyai.springaipoc.chat.dto.ChatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.springframework.ai.chat.client.ChatClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class AnkiMakerService(
    @Qualifier("openAiChatClient") private val openAiChatModel: ChatClient,
    @Qualifier("perplexitySearchRestClient") private val perplexitySearchRestClient: RestClient
) {

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

    private suspend fun generateAnkiContent(request: ChatRequest): ChatResponse = withContext(Dispatchers.IO) {
        val ankiContent = openAiChatModel.prompt()
            .system(ANKI_PROMPT)
            .user(request.message)
            .call()
            .content()
            ?: throw IllegalStateException("ChatGPT 응답 생성 실패")

        ChatResponse(message = ankiContent)
    }

    /**
     * Perplexity Search API를 사용하여 검색을 수행합니다.
     *
     * 검색 성능 최적화 설정:
     * - maxResults: 10개의 검색 결과 반환 (적절한 다양성 확보)
     * - maxTokens: 25,000 토큰 (충분한 컨텍스트 정보)
     * - maxTokensPerPage: 2,048 토큰 (각 페이지당 적절한 요약 길이)
     * - searchRecencyFilter: "month" (최근 1개월 내 최신 정보로 Anki 카드 품질 향상)
     * - searchLanguageFilter: ["en", "ko"] (영어와 한국어 결과 모두 활용)
     *
     * @param request 사용자 검색 요청
     * @return 포맷팅된 검색 결과를 포함하는 ChatResponse
     * @throws IllegalStateException 검색 API 호출 실패시
     */
    private suspend fun search(request: ChatRequest): ChatResponse = withContext(Dispatchers.IO) {
        val searchRequest = PerplexitySearchRequest(
            query = request.message,
            maxResults = 10,
            maxTokens = 25000,
            maxTokensPerPage = 2048,
            searchRecencyFilter = "month",
            searchLanguageFilter = listOf("en", "ko")
        )

        val searchResponse = perplexitySearchRestClient
            .post()
            .uri("/search")
            .body(searchRequest)
            .retrieve()
            .body(PerplexitySearchResponse::class.java)
            ?: throw IllegalStateException("Perplexity Search API 응답 생성 실패")

        val formattedContent = formatSearchResults(searchResponse)
        ChatResponse(message = formattedContent)
    }

    /**
     * 검색 결과를 읽기 쉬운 형식으로 포맷팅합니다.
     *
     * 각 검색 결과는 번호, 제목, URL, 요약, 업데이트 날짜를 포함합니다.
     *
     * @param response Perplexity Search API 응답
     * @return 포맷팅된 검색 결과 문자열
     */
    private fun formatSearchResults(response: PerplexitySearchResponse): String {
        return buildString {
            appendLine("=== 검색 결과 ===\n")
            response.results.forEachIndexed { index, result ->
                appendLine("# ${index + 1}. ${result.title}\n")
                appendLine("## URL: ${result.url}\n")
                appendLine("## 요약: \n${result.snippet}")
                appendLine("## 마지막 업데이트: \n${result.lastUpdated}")
                appendLine()
            }
            appendLine("총 ${response.results.size}개의 검색 결과")
        }
    }
}
