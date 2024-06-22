package org.pets.history.service

import org.pets.history.repository.OwnerRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class CustomUserDetailsService(
    private val ownerRepository: OwnerRepository,
) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val user = ownerRepository.findByEmail(email).orElseThrow {
            UsernameNotFoundException("No se encontró el usuario")
        }
        return User(user.name, user.password, listOf())
    }
}
