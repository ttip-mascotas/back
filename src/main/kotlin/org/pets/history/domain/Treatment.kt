package org.pets.history.domain

import com.fasterxml.jackson.annotation.JsonView
import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotEmpty
import org.pets.history.serializer.View
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Entity
@NamedEntityGraphs(
        NamedEntityGraph(
                name = "join", attributeNodes = [
            NamedAttributeNode("calendar"),
                ]
        )
)
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

    @JoinColumn(name = "calendar_id")
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonView(View.Extended::class)
    var calendar: TreatmentCalendar? = null

    fun start() {
        val schedulesPerDay = schedulesPerDays(organizeByDate())

        calendar = TreatmentCalendar().apply {
            this.schedulesPerDay = schedulesPerDay
        }
    }

    private fun organizeByDate(): HashMap<String, MutableSet<LocalDateTime>> {
        val schedule: HashMap<String, MutableSet<LocalDateTime>> = hashMapOf()
        var lastDate: LocalDateTime = datetime
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (number in 1..numberOfTimes) {
            addDateTimeByDate(formatter, lastDate, schedule)
            lastDate = lastDate.plusHours(frequency.toLong())
        }

        return schedule
    }

    private fun addDateTimeByDate(formatter: DateTimeFormatter, lastDate: LocalDateTime, list: HashMap<String, MutableSet<LocalDateTime>>) {
        val key = formatter.format(lastDate)
        if (list.containsKey(key)) {
            list[key]?.add(lastDate)
        } else {
            list[key] = mutableSetOf(lastDate)
        }
    }

    private fun schedulesPerDays(dates: HashMap<String, MutableSet<LocalDateTime>>) =
            dates.keys.map {
                SchedulePerDay().apply {
                    date = LocalDate.parse(it)
                    doseControllers = dates[it]?.map {
                        DoseControl().apply {
                            time = it
                            supplied = false
                        }
                    }?.toMutableSet() ?: mutableSetOf()
                }
            }.toMutableSet()
}