package org.pets.history.controller

import com.fasterxml.jackson.annotation.JsonView
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.pets.history.domain.FamilyGroup
import org.pets.history.serializer.View
import org.pets.history.service.FamilyGroupService
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@Tag(name = "groups", description = "Endpoints for managing family groups")
@CrossOrigin(origins = ["*"])
@RequestMapping("groups")
class GroupController(
        private val familyGroupService: FamilyGroupService,
) {

    @Operation(
            summary = "Get a group",
            description = "Get a group by id",
    )
    @ApiResponses(
            value = [
                ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = [
                            Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = Schema(implementation = FamilyGroup::class),
                            )
                        ]
                )
            ]
    )
    @GetMapping("/{id}")
    @JsonView(View.ExtendedFamilyGroup::class)
    fun getFamilyGroup(@PathVariable id: Long): FamilyGroup = familyGroupService.getGroupById(id)
}