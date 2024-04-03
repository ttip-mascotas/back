package org.pets.history.domain

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Past
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@Entity
class MedicalVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], optional = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("petId")
    var pet: Pet? = null

    @Column(length = 256, nullable = false)
    @NotEmpty(message = "Address must not be empty")
    var address = ""

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Past
    lateinit var datetime: LocalDateTime

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Specialist must not be empty")
    var specialist = ""

    @Column(length = 512, nullable = false)
    @NotEmpty(message = "Observations must not be empty")
    var observations = ""
}
