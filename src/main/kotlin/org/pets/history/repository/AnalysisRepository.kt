package org.pets.history.repository

import org.pets.history.domain.Analysis
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface AnalysisRepository : CrudRepository<Analysis, Long> {
    @Query(
        value = """
            SELECT *
            FROM analysis a
            WHERE a.pet_id = :petId
            AND TO_TSVECTOR('spanish', a.name || ' ' || a.text) @@ WEBSEARCH_TO_TSQUERY('spanish', :query)
        """,
        nativeQuery = true
    )
    fun search(@Param("petId") petId: Long, @Param("query") query: String): Iterable<Analysis>

    @EntityGraph(
        attributePaths = ["images"],
        type = EntityGraph.EntityGraphType.FETCH,
    )
    fun findWithAllRelatedById(analysisId: Long): Optional<Analysis>
}
