package org.pets.history.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.pets.history.domain.MedicalVisit
import org.pets.history.domain.Pet
import org.pets.history.service.PetService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "pets", description = "Endpoints for managing pets")
@CrossOrigin(origins = ["*"])
@RequestMapping("pets")
@Validated
class PetController(private val petService: PetService) {
    @GetMapping("")
    @Operation(
        summary = "Retrieves all pets",
        description = "Retrieves all registered pets",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = ResponseEntity::class),
                    )
                ]
            )
        ]
    )
    fun getAllPets(): ResponseEntity<String> = ResponseEntity.ok().body("hello world")

    @Operation(
        summary = "Get a pet",
        description = "Get a pet by id",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = Pet::class),
                    )
                ]
            )
        ]
    )
    @GetMapping("/{id}")
    fun getPet(@PathVariable id: Long): Pet = petService.getPet(id)

    @Operation(
        summary = "Registers a medical record",
        description = "Registers a medical record for a given pet id",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Success",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = MedicalVisit::class),
                    )
                ]
            )
        ]
    )
    @PostMapping("/{petId}/medical-records")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPetMedicalRecord(
        @PathVariable petId: Long,
        @RequestBody @Valid medicalVisitIn: MedicalVisit
    ): MedicalVisit = petService.registerMedicalVisit(petId, medicalVisitIn)
}
