package org.pets.history.repository

import org.pets.history.domain.Analysis
import org.springframework.data.repository.CrudRepository

interface AnalysisRepository : CrudRepository<Analysis, Long>
