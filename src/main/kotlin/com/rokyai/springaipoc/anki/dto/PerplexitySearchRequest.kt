package com.rokyai.springaipoc.anki.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Perplexity Search API 요청 DTO
 *
 * 검색 성능 최적화를 위한 파라미터를 포함합니다.
 *
 * @property query 검색 쿼리 문자열
 * @property maxResults 반환할 최대 검색 결과 수 (1-20)
 * @property maxTokens 전체 웹페이지 컨텐츠의 최대 토큰 수. 높은 값은 snippet에 더 많은 내용을 포함
 * @property maxTokensPerPage 각 웹페이지에서 추출할 최대 토큰 수
 * @property searchRecencyFilter 최신성 필터 (day/week/month/year). Anki 카드 생성시 최신 정보 확보
 * @property searchLanguageFilter 언어 필터 (ISO 639-1 코드). 한국어(ko)와 영어(en) 결과 포함
 */
data class PerplexitySearchRequest(
    @JsonProperty("query")
    val query: String,

    @JsonProperty("max_results")
    val maxResults: Int = 10,

    @JsonProperty("max_tokens")
    val maxTokens: Int = 25000,

    @JsonProperty("max_tokens_per_page")
    val maxTokensPerPage: Int = 2048,

    @JsonProperty("search_recency_filter")
    val searchRecencyFilter: String? = "month",

    @JsonProperty("search_language_filter")
    val searchLanguageFilter: List<String>? = listOf("en", "ko")
)
