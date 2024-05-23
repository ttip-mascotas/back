package org.pets.history.domain

import jakarta.persistence.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

@Entity
class SchedulePerDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    lateinit var date: LocalDate

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy(value = "time ASC")
    @JoinColumn(name = "schedule_per_day_id")
    var doseControls: MutableSet<DoseControl> = mutableSetOf()
}
