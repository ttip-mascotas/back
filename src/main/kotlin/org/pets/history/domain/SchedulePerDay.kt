package org.pets.history.domain
import jakarta.persistence.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

@Entity
class SchedulePerDay {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    lateinit var date: LocalDate
    @JoinColumn(name = "schedule_per_day_id")
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    lateinit var doseControllers: MutableSet<DoseControl>
}