package org.pets.history.service

import jakarta.validation.Valid
import org.pets.history.domain.MedicalVisit
import org.pets.history.repository.MedicalVisitRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import org.springframework.web.server.ResponseStatusException

@Service
@Validated
class MedicalVisitService(
    private val medicalVisitRepository: MedicalVisitRepository,
    private val petService: PetService
) {
    fun saveMedicalVisit(petId: Long, @Valid medicalVisit: MedicalVisit): MedicalVisit {
        try {
            val foundPet = petService.getPet(petId)
            medicalVisit.pet = foundPet
            return medicalVisitRepository.save(medicalVisit)
        } catch (e: NotFoundException) {
            throw e
        } catch (e: Exception) {
            throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred while registering a medical visit"
            )
        }
    }
}
