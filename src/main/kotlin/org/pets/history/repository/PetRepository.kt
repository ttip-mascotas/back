package org.pets.history.repository

import org.pets.history.domain.Pet
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.CrudRepository
import java.util.*

interface PetRepository : CrudRepository<Pet, Long> {
    @EntityGraph(value = "joinWithMedicalVisitsAndTreatments", type = EntityGraph.EntityGraphType.FETCH)
    fun findWithMedicalVisitsAndTreatmentsById(petId: Long): Optional<Pet>
}
