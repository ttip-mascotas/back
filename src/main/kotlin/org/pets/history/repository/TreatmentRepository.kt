package org.pets.history.repository

import org.pets.history.domain.Treatment
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TreatmentRepository : CrudRepository<Treatment, Long> {
    @EntityGraph(
        attributePaths = ["schedulesPerDay", "schedulesPerDay.doseControls"],
        type = EntityGraph.EntityGraphType.FETCH,
    )
    fun findWithAllRelatedById(petId: Long): Optional<Treatment>
}
