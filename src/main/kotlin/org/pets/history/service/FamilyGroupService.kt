package org.pets.history.service

import jakarta.transaction.Transactional
import org.pets.history.domain.FamilyGroup
import org.pets.history.domain.Pet
import org.pets.history.repository.FamilyGroupRepository
import org.pets.history.repository.PetRepository
import org.springframework.stereotype.Service

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class FamilyGroupService(
    private val familyGroupRepository: FamilyGroupRepository,
        private val petRepository: PetRepository,
) {
    fun getGroupsForUser(ownerId: Long): List<FamilyGroup> {
        return familyGroupRepository.findAllByMember(ownerId)
    }

    fun getGroupById(id: Long): FamilyGroup {
        return familyGroupRepository.findWithAllRelatedById(id).orElseThrow {
            NotFoundException("No existe el grupo con identificador $id")
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    fun registerPet(id: Long, pet: Pet): Pet {
        val foundGroup = getGroupById(id)
        foundGroup.addPet(pet)
        petRepository.save(pet)
        return pet
    }
}
