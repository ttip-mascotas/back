package org.pets.history.repository

import org.pets.history.domain.FamilyGroup
import org.springframework.data.repository.CrudRepository

interface FamilyGroupRepository : CrudRepository<FamilyGroup, Long> {
}