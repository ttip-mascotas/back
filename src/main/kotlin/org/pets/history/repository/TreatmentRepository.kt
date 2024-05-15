package org.pets.history.repository

import org.pets.history.domain.Treatment
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TreatmentRepository : CrudRepository<Treatment, Long> {
    @EntityGraph(value = "join", type = EntityGraph.EntityGraphType.FETCH)
    fun findWithCalendarById(petId: Long): Optional<Treatment>
}