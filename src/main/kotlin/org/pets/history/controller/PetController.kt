package org.pets.history.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.pets.history.serializer.MedicalRecordCreateDTO
import org.pets.history.serializer.MedicalRecordDTO
import org.pets.history.serializer.PetDTO
import org.pets.history.service.MedicalRecordService
import org.pets.history.service.PetService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "pets", description = "Endpoints for managing pets")
@CrossOrigin(origins = ["*"])
@RequestMapping("pets")
class PetController(
    private val petService: PetService,
    private val medicalRecordService: MedicalRecordService,
) {

    @GetMapping("")
    @Operation(
        summary = "Retrieves all pets",
        description = "Retrieves all registered pets",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
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
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = PetDTO::class),
                    )
                ]
            )
        ]
    )
    @GetMapping("/{id}")
    fun getPet(@PathVariable id: Long): PetDTO = PetDTO(petService.getPet(id))

    @Operation(
        summary = "Registers a medical record",
        description = "Registers a medical record for a given pet id",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "success",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = MedicalRecordDTO::class),
                    )
                ]
            )
        ]
    )
    @PostMapping("/{petId}/medical-records")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPetMedicalRecord(
        @PathVariable petId: Long,
        @RequestBody medicalRecordCreateDTO: MedicalRecordCreateDTO
    ): MedicalRecordDTO =
        MedicalRecordDTO.fromMedicalRecord(medicalRecordService.saveMedicalRecord(petId, medicalRecordCreateDTO))

}
