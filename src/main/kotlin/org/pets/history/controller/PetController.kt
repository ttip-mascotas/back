package org.pets.history.controller


import com.fasterxml.jackson.annotation.JsonView
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.pets.history.domain.Analysis
import org.pets.history.domain.MedicalVisit
import org.pets.history.domain.Pet
import org.pets.history.domain.Treatment
import org.pets.history.serializer.CollectionDTO
import org.pets.history.serializer.FileDTO
import org.pets.history.serializer.View
import org.pets.history.service.MinioService
import org.pets.history.service.PetService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


@RestController
@Validated
@Tag(name = "pets", description = "Endpoints for managing pets")
@CrossOrigin(origins = ["*"])
@RequestMapping("pets")
class PetController(
    private val petService: PetService,
    private val minioService: MinioService
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
                description = "Success",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CollectionDTO::class),
                    )
                ]
            )
        ]
    )
    @JsonView(View.Compact::class)
    fun getAllPets(): CollectionDTO<Pet> = CollectionDTO(petService.getAllPets())

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
    @JsonView(View.Extended::class)
    fun getPet(@PathVariable id: Long): Pet = petService.getPet(id)

    @Operation(
        summary = "Registers a pet",
        description = "Registers a pet",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
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
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(View.Compact::class)
    fun registerPet(@RequestBody @Valid pet: Pet): Pet = petService.registerPet(pet)

    @GetMapping("/{petId}/medical-records")
    @Operation(
        summary = "Retrieves medical visits",
        description = "Retrieves all medical visits for a given pet id",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Success",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = Schema(implementation = CollectionDTO::class),
                    )
                ]
            )
        ]
    )
    fun getMedicalVisitsForPetId(@PathVariable petId: Long): CollectionDTO<MedicalVisit> =
        CollectionDTO(petService.getMedicalVisits(petId))

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
    fun registerPetMedicalVisit(
        @PathVariable petId: Long,
        @RequestBody @Valid medicalVisitIn: MedicalVisit
    ): MedicalVisit = petService.registerMedicalVisit(petId, medicalVisitIn)

    @Operation(
        summary = "Start a treatment",
        description = "Start a treatment for a given pet id",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
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
    @PostMapping("/{petId}/treatments")
    @ResponseStatus(HttpStatus.CREATED)
    fun startTreatment(
        @PathVariable petId: Long,
        @RequestBody @Valid treatment: Treatment
    ): Treatment = petService.startTreatment(petId, treatment)

    @PostMapping(
        "/avatars",
        consumes = [MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadAvatar(
        @RequestPart("avatar") avatar: MultipartFile
    ): FileDTO = FileDTO(minioService.uploadAvatar(avatar.inputStream, avatar.contentType!!))

    @PostMapping(
        "/{petId}/analyses",
        consumes = [MediaType.APPLICATION_PDF_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun attachAnalysis(
        @PathVariable petId: Long,
        @RequestPart("analysis") analysis: MultipartFile,
    ): Analysis = petService.attachAnalysis(petId, analysis)
}
