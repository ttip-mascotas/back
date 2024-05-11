package org.pets.history.repository

import org.pets.history.domain.Analysis
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface AnalysisRepository : CrudRepository<Analysis, Long> {
    @Query(
        value = """
            SELECT * 
            FROM Analysis a 
            WHERE a.pet_id = :petId 
            AND to_tsvector('spanish', a.name || ' ' || a.text) @@ websearch_to_tsquery('spanish', :query)
        """,
        nativeQuery = true
    )
    fun search(@Param("petId") petId: Long, @Param("query") query: String): Iterable<Analysis>
}
