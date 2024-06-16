package org.pets.history.domain

import jakarta.persistence.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.OffsetDateTime

@Entity
class TreatmentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var datetime: OffsetDateTime = OffsetDateTime.MIN

    @Column(nullable = false)
    var administered: Boolean = false
}
