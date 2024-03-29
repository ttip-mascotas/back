package org.pets.history.repository

import org.pets.history.domain.Pet
import org.springframework.data.repository.CrudRepository

interface PetRepository : CrudRepository<Pet, Long>
