package org.pets.history.service
import jakarta.transaction.Transactional
import org.pets.history.domain.Treatment
import org.pets.history.repository.TreatmentRepository
import org.springframework.stereotype.Service

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class TreatmentService(
        private val treatmentRepository: TreatmentRepository
) {

    fun getTreatment(treatmentId: Long): Treatment {
        return treatmentRepository.findWithCalendarById(treatmentId).orElseThrow {
            NotFoundException("No existe el tratamiento con identificador $treatmentId")
        }
    }
}