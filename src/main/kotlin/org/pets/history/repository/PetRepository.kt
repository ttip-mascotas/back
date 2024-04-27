package org.pets.history.repository

import org.pets.history.domain.Pet
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PetRepository : CrudRepository<Pet, Long> {
    @EntityGraph(value = "joinAll", type = EntityGraph.EntityGraphType.FETCH)
    fun findWithAllRelatedById(petId: Long): Optional<Pet>
}
