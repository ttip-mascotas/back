package org.pets.history.domain

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

    @ManyToOne(cascade = [CascadeType.MERGE])
    @JoinColumn(name = "pet_id", referencedColumnName = "id", nullable = false)
    lateinit var pet: Pet

    @NotEmpty(message = "Address must not be empty")
    @Column(length = 256, nullable = false)
    var address = ""

    @Past
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    lateinit var datetime: LocalDateTime

    @NotEmpty(message = "Specialist must not be empty")
    @Column(length = 128, nullable = false)
    var specialist = ""

    @NotEmpty(message = "Observations must not be empty")
    @Column(length = 512, nullable = false)
    var observations = ""
}
