package com.rokyai.springaipoc.anki.controller

import com.rokyai.springaipoc.anki.service.AnkiMakerService
import com.rokyai.springaipoc.chat.dto.ChatRequest
import com.rokyai.springaipoc.common.service.FileDownloadService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Anki 암기 카드와 검색 결과를 생성하고 다운로드하는 API Controller
 *
 * 사용자 메시지를 기반으로 Anki 암기 카드와 검색 결과를 생성하여
 * 두 개의 Markdown 파일을 zip으로 압축하여 다운로드할 수 있는 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/anki")
@Tag(name = "Anki API", description = "Anki 암기 카드와 검색 결과 생성 API")
class AnkiController(
    private val ankiMakerService: AnkiMakerService,
    private val fileDownloadService: FileDownloadService
) {
    /**
     * Anki 암기 카드와 검색 결과를 생성하고 zip 파일로 다운로드합니다.
     *
     * 두 개의 Markdown 파일(anki.md, search.md)을 생성하여 하나의 zip 파일로 압축합니다.
     *
     * @param request 사용자가 보낼 메시지를 포함한 요청 객체
     * @return Anki 콘텐츠와 검색 결과가 포함된 zip 파일
     */
    @PostMapping("/download")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Anki 암기 카드 및 검색 결과 패키지 다운로드",
        description = "사용자 메시지를 기반으로 Anki 암기 카드와 검색 결과를 생성하여 " +
                "두 개의 Markdown 파일을 zip으로 압축하여 다운로드합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Anki 패키지 파일이 정상적으로 생성되었습니다. " +
                        "zip 파일 내부에는 anki.md와 search.md 두 파일이 포함됩니다.",
                content = [Content(
                    mediaType = "application/zip",
                    schema = Schema(type = "string", format = "binary")
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청입니다. 메시지가 비어있거나 형식이 올바르지 않습니다.",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "500",
                description = "서버 내부 오류가 발생했습니다. Anki 카드 생성 실패, 검색 실패 또는 파일 생성 실패",
                content = [Content()]
            )
        ]
    )
    suspend fun downloadAnkiPackage(
        @Valid @RequestBody request: ChatRequest
    ): ResponseEntity<ByteArrayResource> {
        val response = ankiMakerService.searchAndGenerateAnki(request)

        val files = mapOf(
            "anki.md" to response.ankiContent,
            "search.md" to response.searchContent
        )

        return fileDownloadService.createZipFileResponse(files, "anki_package")
    }
}
