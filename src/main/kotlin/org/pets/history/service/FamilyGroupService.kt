package org.pets.history.service

import jakarta.transaction.Transactional
import org.pets.history.domain.FamilyGroup
import org.pets.history.repository.FamilyGroupRepository
import org.springframework.stereotype.Service

@Service
@Transactional(Transactional.TxType.SUPPORTS)
class FamilyGroupService(
        private val familyGroupRepository: FamilyGroupRepository,
) {

    fun getGroupsForUser(ownerId: Long): List<FamilyGroup> {
        return familyGroupRepository.findAllByMember(ownerId)
    }

    fun getGroupById(id: Long): FamilyGroup {
        return familyGroupRepository.findWithAllRelatedById(id).orElseThrow {
            NotFoundException("No existe el grupo con identificador $id")
        }
    }
}