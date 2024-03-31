package org.pets.history.service

import org.pets.history.domain.MedicalRecord
import org.pets.history.repository.MedicalRecordRepository
import org.pets.history.serializer.MedicalRecordCreateDTO
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class MedicalRecordService(
    private val medicalRecordRepository: MedicalRecordRepository,
    private val petService: PetService
) {

    fun saveMedicalRecord(petId: Long, medicalRecordCreateDTO: MedicalRecordCreateDTO): MedicalRecord {
        try {
            val foundPet = petService.getPet(petId)
            val newMedicalRecord = MedicalRecord().apply {
                pet = foundPet
                address = medicalRecordCreateDTO.address
                datetime = medicalRecordCreateDTO.datetime
                observations = medicalRecordCreateDTO.observations
            }
            return medicalRecordRepository.save(newMedicalRecord)
        } catch (e: NotFoundException) {
            throw e
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado")
        }
    }

}
