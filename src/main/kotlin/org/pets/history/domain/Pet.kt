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

    @Column(length = 80)
    var name = ""

    @Column(columnDefinition="TEXT")
    var photo = ""

    @Column
    @DecimalMin(value = "0.0")
    var weight: Double = 0.0

    @Column(nullable = false)
    @DateTimeFormat
    var birthdate: LocalDate? = null

    @Column(length = 128)
    var breed: String = ""

    @Column(length = 128)
    var fur: String = ""

    @Column(nullable = false)
    var sex: PetSex? = null

    fun age(): Int? = this.birthdate?.let {
        Period.between(LocalDate.now(), it).years.absoluteValue
    }
}

enum class PetSex {
    FEMALE, MALE
}