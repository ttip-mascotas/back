package org.pets.history.domain
import jakarta.persistence.*

@Entity
class TreatmentCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    @JoinColumn(name = "treatment_calendar_id")
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    lateinit var schedulesPerDay: MutableSet<SchedulePerDay>
}