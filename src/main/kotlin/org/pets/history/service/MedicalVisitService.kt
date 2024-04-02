package org.pets.history.service

import org.pets.history.domain.MedicalVisit
import org.pets.history.repository.MedicalVisitRepository
import org.pets.history.serializer.MedicalVisitCreateRequestDTO
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class MedicalVisitService(
    private val medicalVisitRepository: MedicalVisitRepository,
    private val petService: PetService
) {

    fun saveMedicalVisit(petId: Long, medicalVisitCreateRequestDTO: MedicalVisitCreateRequestDTO): MedicalVisit {
        try {
            val foundPet = petService.getPet(petId)
            val newMedicalRecord = MedicalVisit().apply {
                pet = foundPet
                address = medicalVisitCreateRequestDTO.address
                datetime = medicalVisitCreateRequestDTO.datetime
                observations = medicalVisitCreateRequestDTO.observations
            }
            return medicalVisitRepository.save(newMedicalRecord)
        } catch (e: NotFoundException) {
            throw e
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado")
        }
    }

}
