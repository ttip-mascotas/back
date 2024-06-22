package org.pets.history.controller

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.pets.history.serializer.AuthenticationDTO
import org.pets.history.serializer.LoginDTO
import org.pets.history.service.AuthService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
@Tag(name = "auth", description = "Endpoints for managing authentication")
@CrossOrigin(origins = ["*"])
@Validated
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDTO: LoginDTO): AuthenticationDTO =
        AuthenticationDTO(authService.authenticate(loginDTO.username, loginDTO.password))
}
