package org.pets.history.domain

import jakarta.persistence.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@Entity
class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @ManyToOne(cascade = [CascadeType.MERGE])
    @JoinColumn(name = "pet_id", nullable = false)
    lateinit var pet: Pet

    @Column(length = 256)
    var address = ""

    @Column(nullable = false)
    @DateTimeFormat
    var datetime: LocalDateTime? = null

    @Column(length = 128)
    var specialist = ""

    @Column(length = 512)
    var observations = ""
}
