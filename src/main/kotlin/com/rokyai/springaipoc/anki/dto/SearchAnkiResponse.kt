package com.rokyai.springaipoc.anki.dto

/**
 * 검색 결과와 Anki 암기 카드를 포함하는 응답 DTO
 *
 * @property searchContent 검색 결과 내용
 * @property ankiContent 생성된 Anki 카드 내용
 */
data class SearchAnkiResponse(
    val searchContent: String,
    val ankiContent: String
)
