package org.pets.history.repository

import org.pets.history.domain.Pet
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PetRepository : CrudRepository<Pet, Long> {

    override fun findById(id: Long): Optional<Pet>

}