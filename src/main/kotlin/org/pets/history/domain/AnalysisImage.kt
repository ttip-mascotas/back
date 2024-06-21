package org.pets.history.domain

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import org.hibernate.annotations.CreationTimestamp
import org.springframework.format.annotation.DateTimeFormat
import java.time.OffsetDateTime

@Entity
class AnalysisImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(length = 256, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas una ruta de archivo")
    var url: String = ""

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreationTimestamp
    var createdAt: OffsetDateTime = OffsetDateTime.now()
}
