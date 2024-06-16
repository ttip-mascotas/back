package org.pets.history.controller

import com.fasterxml.jackson.annotation.JsonView
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.pets.history.domain.FamilyGroup
import org.pets.history.serializer.CollectionDTO
import org.pets.history.serializer.View
import org.pets.history.service.FamilyGroupService
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@Tag(name = "users", description = "Endpoints for managing users")
@CrossOrigin(origins = ["*"])
@RequestMapping("users")
class UserController(
        private val familyGroupService: FamilyGroupService,
) {
    @Operation(
            summary = "Retrieves all groups for a user",
            description = "Retrieves all groups for a user",
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
    @GetMapping("/{id}/groups")
    @JsonView(View.CompactFamilyGroup::class)
    fun getAllGroupsForAUser(@PathVariable id: Long): CollectionDTO<FamilyGroup> = CollectionDTO(familyGroupService.getGroupsForUser(id))
}