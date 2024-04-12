package org.pets.history.service

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.pets.history.domain.MedicalVisit
import org.pets.history.domain.Pet
import org.pets.history.repository.MedicalVisitRepository
import org.pets.history.repository.PetRepository
import org.springframework.stereotype.Service

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class PetService(private val petRepository: PetRepository, private val medicalVisitRepository: MedicalVisitRepository) {
    fun getAllPets(): MutableIterable<Pet> = petRepository.findAll()

    fun getPet(id: Long): Pet = petRepository.findWithMedicalVisitsById(id).orElseThrow {
        NotFoundException("No existe la mascota con identificador $id")
    }

    @Transactional(Transactional.TxType.REQUIRED)
    fun registerPet(@Valid pet: Pet): Pet {
        try {
            return petRepository.save(pet)
        } catch (e: Exception) {
            throw UnexpectedException()
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    fun registerMedicalVisit(petId: Long, @Valid medicalVisit: MedicalVisit): MedicalVisit {
        try {
            val foundPet = getPet(petId)
            foundPet.addMedicalVisit(medicalVisit)
            medicalVisitRepository.save(medicalVisit)
            return medicalVisit
        } catch (e: NotFoundException) {
            throw e
        } catch (e: Exception) {
            throw UnexpectedException()
        }
    }

    fun getMedicalVisits(petId: Long): Iterable<MedicalVisit> = getPet(petId).medicalVisits
}
