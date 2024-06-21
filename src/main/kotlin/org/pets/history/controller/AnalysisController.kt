package org.pets.history.controller

import com.fasterxml.jackson.annotation.JsonView
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.pets.history.domain.Analysis
import org.pets.history.serializer.View
import org.pets.history.service.AnalysisService
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@RestController
@Validated
@Tag(name = "analyses", description = "Endpoints for managing analyses")
@CrossOrigin(origins = ["*"])
@RequestMapping("analyses")
class AnalysisController(
    private val analysisService: AnalysisService
) {
    @Operation(
        summary = "Get an analysis",
        description = "Gets an analysis by id",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = Analysis::class),
                    )
                ]
            )
        ]
    )
    @GetMapping("/{analysisId}")
    @JsonView(View.ExtendedAnalysis::class)
    fun getAnalysis(@PathVariable analysisId: Long): Analysis =
        analysisService.getAnalysis(analysisId)
}
