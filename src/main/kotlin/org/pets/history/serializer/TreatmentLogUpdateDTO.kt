package org.pets.history.serializer

import com.fasterxml.jackson.annotation.JsonProperty

data class TreatmentLogUpdateDTO(
    @JsonProperty(required = true)
    val administered: Boolean
)
