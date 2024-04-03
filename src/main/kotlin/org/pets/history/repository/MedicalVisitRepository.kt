package org.pets.history.repository

import org.pets.history.domain.MedicalVisit
import org.springframework.data.repository.CrudRepository

interface MedicalVisitRepository : CrudRepository<MedicalVisit, Long> {
    fun findAllByPetIdOrderByDatetimeDesc(petId: Long): Collection<MedicalVisit>
}
