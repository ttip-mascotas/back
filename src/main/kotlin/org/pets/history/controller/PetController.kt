package org.pets.history.controller


import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.pets.history.serializer.PetDTO
import org.pets.history.service.PetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "pets", description = "Endpoints for managing pets")
@CrossOrigin(origins = ["*"])
@RequestMapping("pets")
class PetController {
    @Autowired
    private lateinit var petService: PetService

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
    fun getAllPets(): ResponseEntity<String> {
        return ResponseEntity.ok().body("hello world")
    }

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
}
