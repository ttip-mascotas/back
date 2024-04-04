package org.pets.history.domain

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Past
import org.pets.history.serializer.View
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
    @NotEmpty(message = "Es necesario que introduzcas un nombre")
    var name = ""

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotEmpty(message = "Es necesario que cargues una foto")
    var photo = ""

    @Column(nullable = false)
    @DecimalMin(value = "0.1", message = "Es necesario que peso sea mayor o igual a 0.1 Kg")
    var weight: Double = 0.0

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Past
    var birthdate: LocalDate = LocalDate.MIN

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que cargues una raza")
    var breed: String = ""

    @Column(length = 128, nullable = false)
    var fur: String = ""

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var sex: PetSex = PetSex.FEMALE

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn
    @OrderBy(value = "datetime DESC")
    @JsonView(View.Extended::class)
    var medicalVisits: MutableSet<MedicalVisit> = mutableSetOf()

    val age
        @JsonProperty
        get(): Int = Period.between(LocalDate.now(), this.birthdate).years.absoluteValue

    fun addMedicalVisit(medicalVisit: MedicalVisit) {
        medicalVisits.add(medicalVisit)
    }
}

enum class PetSex {
    FEMALE, MALE
}
