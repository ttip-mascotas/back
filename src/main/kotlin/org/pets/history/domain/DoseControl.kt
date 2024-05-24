package org.pets.history.domain

import jakarta.persistence.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@Entity
class DoseControl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    lateinit var time: LocalDateTime

    @Column(nullable = false)
    var supplied: Boolean = false
}
