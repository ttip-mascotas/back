package org.pets.history.serializer

import java.time.LocalDateTime

data class MedicalRecordCreateDTO(
    val address: String,
    val datetime: LocalDateTime,
    val specialist: String,
    val observations: String,
)
