package org.pets.history.service

import jakarta.transaction.Transactional
import org.pets.history.domain.Treatment
import org.pets.history.domain.TreatmentLog
import org.pets.history.repository.TreatmentLogRepository
import org.pets.history.repository.TreatmentRepository
import org.pets.history.serializer.TreatmentLogUpdateDTO
import org.springframework.stereotype.Service

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class TreatmentService(
    private val treatmentRepository: TreatmentRepository,
    private val treatmentLogRepository: TreatmentLogRepository,
) {
    fun getTreatment(treatmentId: Long): Treatment {
        return treatmentRepository.findWithAllRelatedById(treatmentId).orElseThrow {
            NotFoundException("No existe el tratamiento con identificador $treatmentId")
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    fun updateTreatmentLog(
        treatmentId: Long,
        treatmentLogId: Long,
        treatmentLogUpdateDTO: TreatmentLogUpdateDTO
    ): TreatmentLog {
        val treatmentLog = treatmentLogRepository.findByTreatmentIdAndId(treatmentId, treatmentLogId).orElseThrow {
            NotFoundException("No existe el log de tratamiento con identificador $treatmentId y $treatmentLogId")
        }
        with(treatmentLog) {
            administered = treatmentLogUpdateDTO.administered
        }
        return treatmentLogRepository.save(treatmentLog)
    }
}
