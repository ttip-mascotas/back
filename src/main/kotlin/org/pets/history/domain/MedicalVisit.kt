package org.pets.history.domain

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.PastOrPresent
import org.springframework.format.annotation.DateTimeFormat
import java.time.OffsetDateTime

@Entity
class MedicalVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(length = 256, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas la dirección")
    var address = ""

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @PastOrPresent(message = "Es necesario que introduzcas una fecha igual o anterior a hoy")
    var datetime: OffsetDateTime = OffsetDateTime.MIN

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas el nombre del especialista")
    var specialist = ""

    @Column(length = 512, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas las observaciones")
    var observations = ""
}
