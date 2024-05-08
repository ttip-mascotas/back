package org.pets.history.service

import jakarta.transaction.Transactional
import org.apache.pdfbox.Loader
import org.apache.pdfbox.io.RandomAccessReadBuffer
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.pets.history.domain.*
import org.pets.history.repository.*
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class PetService(
    private val petRepository: PetRepository,
    private val medicalVisitRepository: MedicalVisitRepository,
    private val treatmentRepository: TreatmentRepository,
    private val analysisRepository: AnalysisRepository,
    private val analysisIndexRepository: IndexedAnalysisRepository,
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
        val fileText = readText(analysisFile)

        val analysis = Analysis().apply {
            name = analysisFile.originalFilename?.ifBlank { defaultFilename } ?: defaultFilename
            size = analysisFile.size
            url = analysisURL
            text = fileText
        }

        foundPet.attachAnalysis(analysis)
        analysisRepository.save(analysis)

        val indexedAnalysis = IndexedAnalysis().apply {
            analysisId = analysis.id
            text = fileText
        }
        analysisIndexRepository.save(indexedAnalysis)
        
        return analysis
    }

    fun readText(pdfFile: MultipartFile): String {
        val document: PDDocument = Loader.loadPDF(RandomAccessReadBuffer(pdfFile.inputStream))
        val pdfStripper = PDFTextStripper()
        return pdfStripper.getText(document)
    }
}
