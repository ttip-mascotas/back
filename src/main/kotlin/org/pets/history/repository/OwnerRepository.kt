package org.pets.history.repository

import org.pets.history.domain.Owner
import org.springframework.data.repository.CrudRepository

interface OwnerRepository : CrudRepository<Owner, Long> {
}