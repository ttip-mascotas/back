package org.pets.history.domain

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@Entity
class MedicalVisit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(length = 256, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas la dirección")
    var address = ""

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    //TODO: ver porqué la anotación no funciona con la componente de tiempo y/o si hay alternativas
    //@PastOrPresent(message = "Es necesario que introduzcas una fecha y hora igual o anterior a ahora")
    var datetime: LocalDateTime = LocalDateTime.MIN

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas el nombre del especialista")
    var specialist = ""

    @Column(length = 512, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas las observaciones")
    var observations = ""
}
