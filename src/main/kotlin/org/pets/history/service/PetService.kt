package org.pets.history.service

import org.pets.history.domain.Pet
import org.pets.history.repository.PetRepository
import org.springframework.stereotype.Service

@Service
class PetService(private val petRepository: PetRepository) {
    fun getPet(id: Long): Pet = petRepository.findById(id).orElseThrow {
        NotFoundException("Pet with $id does not exist")
    }
}
