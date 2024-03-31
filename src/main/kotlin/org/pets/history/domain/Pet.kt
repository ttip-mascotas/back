package org.pets.history.domain

import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.Period
import kotlin.math.absoluteValue


@Entity
class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(length = 80, nullable = false)
    var name = ""

    @Column(columnDefinition = "TEXT", nullable = false)
    var photo = ""

    @Column(nullable = false)
    @DecimalMin(value = "0.0")
    var weight: Double = 0.0

    @Column(nullable = false)
    @DateTimeFormat
    lateinit var birthdate: LocalDate

    @Column(length = 128, nullable = false)
    var breed: String = ""

    @Column(length = 128, nullable = false)
    var fur: String = ""

    @Column(nullable = false)
    lateinit var sex: PetSex

    @OneToMany(mappedBy = "pet", cascade = [CascadeType.MERGE])
    var medicalRecords: Set<MedicalRecord> = mutableSetOf()

    fun age(): Int = Period.between(LocalDate.now(), this.birthdate).years.absoluteValue
}

enum class PetSex {
    FEMALE, MALE
}
