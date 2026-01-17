package com.rokyai.springaipoc.anki.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Perplexity Search API 응답 DTO
 *
 * @property results 검색 결과 리스트
 */
data class PerplexitySearchResponse(
    @JsonProperty("results")
    val results: List<SearchResult>
)

/**
 * 개별 검색 결과
 *
 * @property title 검색 결과 제목
 * @property url 검색 결과 URL
 * @property snippet 컨텐츠 요약 또는 발췌
 * @property date 페이지가 Perplexity 인덱스에 추가된 날짜
 * @property lastUpdated 페이지가 Perplexity 인덱스에서 마지막으로 업데이트된 날짜
 */
data class SearchResult(
    @JsonProperty("title")
    val title: String,

    @JsonProperty("url")
    val url: String,

    @JsonProperty("snippet")
    val snippet: String,

    @JsonProperty("date")
    val date: String,

    @JsonProperty("last_updated")
    val lastUpdated: String
)
