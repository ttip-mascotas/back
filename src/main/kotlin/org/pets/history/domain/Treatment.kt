package org.pets.history.domain

import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotEmpty
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
@Entity
class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas el nombre del medicamento")
    var medicine: String = ""

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "Es necesario que introduzcas una fecha igual o siguiente a hoy")
    var datetime: LocalDateTime = LocalDateTime.MIN

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas la dosis indicada")
    var dose: String = ""

    @Column(nullable = false)
    @DecimalMin(value = "1", message = "Es necesario que introduzcas una frecuencia mayor o igual a 1 hora")
    var frequency: Int = 0

    @Column(nullable = false)
    @DecimalMin(value = "1", message = "Es necesario que introduzcas una cantidad mayor o igual a 1")
    var numberOfTimes: Int = 0
}