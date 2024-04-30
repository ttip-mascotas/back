package org.pets.history.service

import jakarta.transaction.Transactional
import org.pets.history.domain.Analysis
import org.pets.history.domain.MedicalVisit
import org.pets.history.domain.Pet
import org.pets.history.domain.Treatment
import org.pets.history.repository.AnalysisRepository
import org.pets.history.repository.MedicalVisitRepository
import org.pets.history.repository.PetRepository
import org.pets.history.repository.TreatmentRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class PetService(
    private val petRepository: PetRepository,
    private val medicalVisitRepository: MedicalVisitRepository,
    private val treatmentRepository: TreatmentRepository,
    private val analysisRepository: AnalysisRepository,
    private val minioService: MinioService,
) {
    fun getAllPets(): MutableIterable<Pet> = petRepository.findAll()

    fun getPet(id: Long): Pet = petRepository.findWithAllRelatedById(id).orElseThrow {
        NotFoundException("No existe la mascota con identificador $id")
    }

    @Transactional(Transactional.TxType.REQUIRED)
    fun registerPet(pet: Pet): Pet {
        return petRepository.save(pet)
    }

    fun getMedicalVisits(petId: Long): Iterable<MedicalVisit> = getPet(petId).medicalVisits

    @Transactional(Transactional.TxType.REQUIRED)
    fun registerMedicalVisit(petId: Long, medicalVisit: MedicalVisit): MedicalVisit {
        val foundPet = getPet(petId)
        foundPet.addMedicalVisit(medicalVisit)
        medicalVisitRepository.save(medicalVisit)
        return medicalVisit
    }

    @Transactional(Transactional.TxType.REQUIRED)
    fun startTreatment(petId: Long, treatment: Treatment): Treatment {
        val foundPet = getPet(petId)
        foundPet.startTreatment(treatment)
        treatmentRepository.save(treatment)
        return treatment
    }

    @Transactional(Transactional.TxType.REQUIRED)
    fun attachAnalysis(petId: Long, analysisFile: MultipartFile): Analysis {
        val foundPet = getPet(petId)
        val analysisURL = minioService.uploadPetAnalysis(petId, analysisFile.inputStream, analysisFile.contentType!!)
        val defaultFilename = "análisis.pdf"
        val analysis = Analysis().apply {
            name = analysisFile.originalFilename?.ifBlank { defaultFilename } ?: defaultFilename
            url = analysisURL
        }
        foundPet.attachAnalysis(analysis)
        analysisRepository.save(analysis)
        return analysis
    }
}
