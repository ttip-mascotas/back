package org.pets.history.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.PastOrPresent
import org.pets.history.serializer.View
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.Period
import kotlin.math.absoluteValue


@Entity
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "joinAll", attributeNodes = [
            NamedAttributeNode("medicalVisits"),
            NamedAttributeNode("treatments"),
            NamedAttributeNode("analyses"),
        ]
    )
)
class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas un nombre")
    var name = ""

    @Column(length = 256, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas una foto")
    var photo = ""

    @Column(nullable = false)
    @DecimalMin(value = "0.1", message = "Es necesario que introduzcas un peso mayor o igual a 0.1 Kg")
    var weight: Double = 0.0

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @PastOrPresent(message = "Es necesario que introduzcas una fecha de nacimiento igual o anterior a hoy")
    var birthdate: LocalDate = LocalDate.MIN

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas una raza")
    var breed: String = ""

    @Column(length = 128, nullable = false)
    var fur: String = ""

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var sex: PetSex = PetSex.FEMALE

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "pet_id")
    @OrderBy(value = "datetime DESC")
    @JsonView(View.Extended::class)
    var medicalVisits: MutableSet<MedicalVisit> = mutableSetOf()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "pet_id")
    @OrderBy(value = "datetime DESC")
    @JsonView(View.Extended::class)
    var treatments: MutableSet<Treatment> = mutableSetOf()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "pet_id")
    @OrderBy(value = "created_at DESC")
    @JsonView(View.Extended::class)
    var analyses: MutableSet<Analysis> = mutableSetOf()

    val age
        @JsonProperty
        get(): Int = Period.between(LocalDate.now(), this.birthdate).years.absoluteValue

    fun addMedicalVisit(medicalVisit: MedicalVisit) {
        medicalVisits.add(medicalVisit)
    }

    fun startTreatment(treatment: Treatment) {
        treatments.add(treatment)
    }

    fun attachAnalysis(analysis: Analysis) {
        analyses.add(analysis)
    }
}

enum class PetSex {
    FEMALE, MALE
}
