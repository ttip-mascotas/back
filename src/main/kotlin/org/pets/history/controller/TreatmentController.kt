package org.pets.history.controller

import com.fasterxml.jackson.annotation.JsonView
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.pets.history.domain.Treatment
import org.pets.history.domain.TreatmentLog
import org.pets.history.serializer.TreatmentLogUpdateDTO
import org.pets.history.serializer.View
import org.pets.history.service.TreatmentService
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@Tag(name = "treatments", description = "Endpoints for managing treatments")
@CrossOrigin(origins = ["*"])
@RequestMapping("treatments")
class TreatmentController(
    private val treatmentService: TreatmentService
) {
    @Operation(
        summary = "Get a treatment",
        description = "Get a treatment by id",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = Treatment::class),
                    )
                ]
            )
        ]
    )
    @GetMapping("/{treatmentId}")
    @JsonView(View.ExtendedTreatment::class)
    fun getTreatment(@PathVariable treatmentId: Long): Treatment = treatmentService.getTreatment(treatmentId)

    @Operation(
        summary = "Update a treatment log",
        description = "Update a treatment log by id",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = TreatmentLog::class),
                    )
                ]
            )
        ]
    )
    @PutMapping("/{treatmentId}/logs/{treatmentLogId}")
    fun updateTreatmentLog(
        @PathVariable treatmentId: Long,
        @PathVariable treatmentLogId: Long,
        @RequestBody @Valid treatmentLogUpdateDTO: TreatmentLogUpdateDTO
    ): TreatmentLog =
        treatmentService.updateTreatmentLog(treatmentId, treatmentLogId, treatmentLogUpdateDTO)

    @MessageMapping("/treatments/{treatmentId}/logs/{treatmentLogId}")
    @SendTo("/topic/treatments/{treatmentId}")
    fun updateTreatmentLogWS(
        @DestinationVariable treatmentId: Long,
        @DestinationVariable treatmentLogId: Long,
        @Payload @Valid treatmentLogUpdateDTO: TreatmentLogUpdateDTO
    ): Treatment {
        treatmentService.updateTreatmentLog(treatmentId, treatmentLogId, treatmentLogUpdateDTO)
        return treatmentService.getTreatment(treatmentId)
    }
}
