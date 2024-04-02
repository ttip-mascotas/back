package org.pets.history.serializer

import org.pets.history.domain.MedicalVisit
import java.time.LocalDateTime

data class MedicalVisitDTO(
    val id: Long,
    val petId: Long,
    val address: String,
    val datetime: LocalDateTime,
    val specialist: String,
    val observations: String,
) {
    companion object {
        fun fromMedicalVisit(medicalVisit: MedicalVisit): MedicalVisitDTO =
            MedicalVisitDTO(
                id = medicalVisit.id!!,
                petId = medicalVisit.pet.id!!,
                address = medicalVisit.address,
                datetime = medicalVisit.datetime,
                specialist = medicalVisit.specialist,
                observations = medicalVisit.observations,
            )
    }
}
