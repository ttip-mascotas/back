package org.pets.history.repository

import org.pets.history.domain.Treatment
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.repository.CrudRepository
import java.util.*

interface TreatmentRepository : CrudRepository<Treatment, Long> {
    @EntityGraph(
        attributePaths = ["logs"],
        type = EntityGraph.EntityGraphType.FETCH,
    )
    fun findWithAllRelatedById(treatmentId: Long): Optional<Treatment>
}
