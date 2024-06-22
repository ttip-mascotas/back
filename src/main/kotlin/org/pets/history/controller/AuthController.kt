package org.pets.history.controller

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.pets.history.serializer.AuthenticationDTO
import org.pets.history.serializer.LoginDTO
import org.pets.history.service.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
) {
    @PostMapping("/login")
    fun login(@RequestBody @Valid loginDTO: LoginDTO): AuthenticationDTO {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginDTO.username, loginDTO.password)
        )
        val token = jwtService.generateToken(authentication)
        return AuthenticationDTO(token)
    }
}
