package org.pets.history.domain

import com.fasterxml.jackson.annotation.JsonView
import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotEmpty
import org.pets.history.serializer.View
import org.springframework.format.annotation.DateTimeFormat
import java.time.OffsetDateTime


@Entity
class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas el nombre del medicamento")
    var medicine: String = ""

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @FutureOrPresent(message = "Es necesario que introduzcas una fecha igual o siguiente a hoy")
    var datetime: OffsetDateTime = OffsetDateTime.MIN

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas la dosis indicada")
    var dose: String = ""

    @Column(nullable = false)
    @DecimalMin(value = "1", message = "Es necesario que introduzcas una frecuencia mayor o igual a 1 hora")
    var frequency: Int = 0

    @Column(nullable = false)
    @DecimalMin(value = "1", message = "Es necesario que introduzcas una cantidad mayor o igual a 1")
    var numberOfTimes: Int = 0

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "treatment_id")
    @OrderBy(value = "datetime ASC")
    @JsonView(View.ExtendedTreatment::class)
    var logs: MutableSet<TreatmentLog> = mutableSetOf()

    fun start() {
        logs = generateTreatmentLogs()
    }

    private fun generateTreatmentLogs(): MutableSet<TreatmentLog> {
        val logs = (0..<numberOfTimes).map {
            TreatmentLog().apply {
                datetime = this@Treatment.datetime.plusHours(it * frequency.toLong())
            }
        }.toMutableSet()
        return logs
    }
}
