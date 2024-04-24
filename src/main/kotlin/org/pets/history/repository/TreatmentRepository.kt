package org.pets.history.repository

import org.pets.history.domain.Treatment
import org.springframework.data.repository.CrudRepository

interface TreatmentRepository : CrudRepository<Treatment, Long>