package org.pets.history.service

import org.pets.history.domain.Pet
import org.pets.history.repository.PetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PetService {

    @Autowired
    lateinit var petRepository: PetRepository
    fun getPet(id: Long): Pet =
            petRepository.findById(id).orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "La mascota con identificador $id no existe")
            }

}