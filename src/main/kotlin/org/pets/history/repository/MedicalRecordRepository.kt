package org.pets.history.repository

import org.pets.history.domain.MedicalRecord
import org.springframework.data.repository.CrudRepository

interface MedicalRecordRepository : CrudRepository<MedicalRecord, Long>
