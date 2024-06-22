package org.pets.history.service

import jakarta.transaction.Transactional
import org.pets.history.repository.OwnerRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional(Transactional.TxType.SUPPORTS)
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val ownerRepository: OwnerRepository,
    private val jwtService: JwtService,
) {
    fun authenticate(username: String, password: String): String {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )
        val user = ownerRepository.findByEmail(username).orElseThrow {
            NotFoundException("No existe el usuario $username")
        }
        val claims = mapOf(
            "id" to user.id.toString(),
            "name" to user.name,
            "email" to user.email,
        )
        return jwtService.generateToken(claims, user)
    }

}
