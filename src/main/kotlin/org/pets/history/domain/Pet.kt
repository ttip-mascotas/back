package org.pets.history.domain

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Past
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.Period
import kotlin.math.absoluteValue


@Entity
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "joinWithMedicalVisits", attributeNodes = [
            NamedAttributeNode("medicalVisits")
        ]
    )
)
class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Name must not be empty")
    var name = ""

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotEmpty(message = "Photo must not be empty")
    var photo = ""

    @Column(nullable = false)
    @DecimalMin(value = "0.1", message = "Weight must not be less than 0.1 Kg")
    var weight: Double = 0.0

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Past()
    var birthdate: LocalDate = LocalDate.MIN

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Breed must not be empty")
    var breed: String = ""

    @Column(length = 128, nullable = false)
    var fur: String = ""

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var sex: PetSex = PetSex.FEMALE

    @OneToMany(mappedBy = "pet", cascade = [CascadeType.ALL], orphanRemoval = true)
    var medicalVisits: MutableSet<MedicalVisit> = mutableSetOf()

    val age
        @JsonProperty
        get(): Int = Period.between(LocalDate.now(), this.birthdate).years.absoluteValue

    fun addMedicalVisit(medicalVisit: MedicalVisit) {
        medicalVisit.pet = this
        medicalVisits.add(medicalVisit)
    }
}

enum class PetSex {
    FEMALE, MALE
}
