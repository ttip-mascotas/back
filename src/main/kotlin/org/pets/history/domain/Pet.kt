package org.pets.history.domain

import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Past
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.Period
import kotlin.math.absoluteValue


@Entity
class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @NotEmpty(message = "Name must not be empty")
    @Column(length = 128, nullable = false)
    var name = ""

    @NotEmpty(message = "Photo must not be empty")
    @Column(columnDefinition = "TEXT", nullable = false)
    var photo = ""

    @Column(nullable = false)
    @DecimalMin(value = "0.1", message = "Weight must not be less than 0.1 Kg")
    var weight: Double = 0.0

    @Past
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    lateinit var birthdate: LocalDate

    @NotEmpty(message = "Breed must not be empty")
    @Column(length = 128, nullable = false)
    var breed: String = ""

    @Column(length = 128, nullable = false)
    var fur: String = ""

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    lateinit var sex: PetSex

    @OneToMany(mappedBy = "pet", cascade = [CascadeType.MERGE])
    var medicalVisits: Set<MedicalVisit> = mutableSetOf()

    fun age(): Int = Period.between(LocalDate.now(), this.birthdate).years.absoluteValue
}

enum class PetSex {
    FEMALE, MALE
}
