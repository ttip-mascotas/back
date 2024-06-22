package org.pets.history.repository

import org.pets.history.domain.Owner
import org.springframework.data.repository.CrudRepository
import java.util.*

interface OwnerRepository : CrudRepository<Owner, Long> {
    fun findByEmail(username: String): Optional<Owner>
}
