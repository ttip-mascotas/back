package org.pets.history.serializer

import org.pets.history.domain.MedicalRecord
import java.time.LocalDateTime

data class MedicalRecordDTO(
    val id: Long,
    val petId: Long,
    val address: String,
    val datetime: LocalDateTime?,
    val specialist: String,
    val observations: String,
) {
    companion object {
        fun fromMedicalRecord(medicalRecord: MedicalRecord): MedicalRecordDTO =
            MedicalRecordDTO(
                id = medicalRecord.id!!,
                petId = medicalRecord.pet.id!!,
                address = medicalRecord.address,
                datetime = medicalRecord.datetime,
                specialist = medicalRecord.specialist,
                observations = medicalRecord.observations,
            )
    }
}
