package org.pets.history.domain

import jakarta.persistence.*
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

    @Column(length = 256, nullable = false)
    var address = ""

    @Column(nullable = false)
    @DateTimeFormat
    lateinit var datetime: LocalDateTime

    @Column(length = 128, nullable = false)
    var specialist = ""

    @Column(length = 512, nullable = false)
    var observations = ""
}
