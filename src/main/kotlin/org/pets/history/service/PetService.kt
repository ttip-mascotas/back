package org.pets.history.service

import jakarta.transaction.Transactional
import jakarta.validation.Valid
import org.pets.history.domain.MedicalVisit
import org.pets.history.domain.Pet
import org.pets.history.domain.Treatment
import org.pets.history.repository.MedicalVisitRepository
import org.pets.history.repository.PetRepository
import org.pets.history.repository.TreatmentRepository
import org.springframework.stereotype.Service

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class PetService(
        private val petRepository: PetRepository,
        private val medicalVisitRepository: MedicalVisitRepository,
        private val treatmentRepository: TreatmentRepository) {
    fun getAllPets(): MutableIterable<Pet> = petRepository.findAll()

    fun getPet(id: Long): Pet = petRepository.findWithMedicalVisitsAndTreatmentsById(id).orElseThrow {
        NotFoundException("No existe la mascota con identificador $id")
    }

    @Transactional(Transactional.TxType.REQUIRED)
    fun registerPet(@Valid pet: Pet): Pet {
        return petRepository.save(pet)
    }

    fun getMedicalVisits(petId: Long): Iterable<MedicalVisit> = getPet(petId).medicalVisits

    @Transactional(Transactional.TxType.REQUIRED)
    fun registerMedicalVisit(petId: Long, @Valid medicalVisit: MedicalVisit): MedicalVisit {
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
}
