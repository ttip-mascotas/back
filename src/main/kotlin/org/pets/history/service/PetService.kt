package org.pets.history.service

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.pets.history.domain.MedicalVisit
import org.pets.history.domain.Pet
import org.pets.history.repository.PetRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class PetService(private val petRepository: PetRepository) {
    fun getPet(id: Long): Pet = petRepository.findWithMedicalVisitsById(id).orElseThrow {
        NotFoundException("Pet with $id does not exist")
    }

    @Transactional(Transactional.TxType.REQUIRED)
    fun registerMedicalVisit(petId: Long, @Valid medicalVisit: MedicalVisit): MedicalVisit {
        try {
            val foundPet = getPet(petId)
            foundPet.addMedicalVisit(medicalVisit)
            petRepository.save(foundPet)
            return medicalVisit
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
