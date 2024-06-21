package org.pets.history.service

import jakarta.transaction.Transactional
import org.pets.history.domain.*
import org.pets.history.repository.AnalysisRepository
import org.pets.history.repository.MedicalVisitRepository
import org.pets.history.repository.PetRepository
import org.pets.history.repository.TreatmentRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class PetService(
    private val petRepository: PetRepository,
    private val medicalVisitRepository: MedicalVisitRepository,
    private val treatmentRepository: TreatmentRepository,
    private val analysisRepository: AnalysisRepository,
    private val minioService: MinioService,
    private val pdfParser: PDFParser,
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

        val extractedText = pdfParser.extractText(analysisFile)
        val extractedImages = pdfParser.extractImages(analysisFile)

        val analysisURL = minioService.uploadPetAnalysis(petId, analysisFile.inputStream, analysisFile.contentType!!)

        val analysisImages = extractedImages.map {
            val imageStream = ByteArrayOutputStream().use { outputStream ->
                ImageIO.write(it, "png", outputStream)
                ByteArrayInputStream(outputStream.toByteArray())
            }

            val imageURL = minioService.uploadPetAnalysisImage(petId, imageStream, MediaType.IMAGE_PNG_VALUE)
            AnalysisImage().apply {
                url = imageURL
            }
        }

        val defaultFilename = "análisis.pdf"

        val analysis = Analysis().apply {
            name = analysisFile.originalFilename?.ifBlank { defaultFilename } ?: defaultFilename
            size = analysisFile.size
            url = analysisURL
            text = extractedText
        }

        analysisImages.forEach {
            analysis.addImage(it)
        }

        foundPet.attachAnalysis(analysis)
        analysisRepository.save(analysis)
        return analysis
    }

    fun searchAnalyses(petId: Long, query: String): Iterable<Analysis> = analysisRepository.search(petId, query)

    fun getAnalysis(petId: Long, analysisId: Long): Analysis =
        analysisRepository.findWithAllRelatedById(analysisId).orElseThrow {
            NotFoundException("No existe el análisis con identificador $analysisId")
        }
}
