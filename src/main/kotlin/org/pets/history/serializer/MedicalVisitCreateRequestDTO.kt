package org.pets.history.serializer

import java.time.LocalDateTime

data class MedicalVisitCreateRequestDTO(
    val address: String,
    val datetime: LocalDateTime,
    val specialist: String,
    val observations: String,
)
