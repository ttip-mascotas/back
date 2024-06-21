package org.pets.history.service

import jakarta.transaction.Transactional
import org.pets.history.domain.Analysis
import org.pets.history.repository.AnalysisRepository
import org.springframework.stereotype.Service

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class AnalysisService(
    private val analysisRepository: AnalysisRepository,
) {
    fun getAnalysis(analysisId: Long): Analysis =
        analysisRepository.findWithAllRelatedById(analysisId).orElseThrow {
            NotFoundException("No existe el análisis con identificador $analysisId")
        }
}
