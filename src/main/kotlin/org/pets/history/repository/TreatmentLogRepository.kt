package org.pets.history.repository

import org.pets.history.domain.TreatmentLog
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface TreatmentLogRepository : CrudRepository<TreatmentLog, Long> {
    @Query(
        value = """
            SELECT * 
            FROM treatment_log t
            WHERE t.id = :id AND t.treatment_id = :treatmentId
        """,
        nativeQuery = true,
    )
    fun findByTreatmentIdAndId(@Param("treatmentId") treatmentId: Long, @Param("id") id: Long): Optional<TreatmentLog>
}
