package org.pets.history.repository

import org.pets.history.domain.FamilyGroup
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

interface FamilyGroupRepository : CrudRepository<FamilyGroup, Long> {

    @Query("""
        SELECT g
          FROM FamilyGroup g 
               INNER JOIN g.members m
         WHERE m.id = :id
        """)
    fun findAllByMember(id: Long): List<FamilyGroup>

    @EntityGraph(value = "joinGroupWithAll", type = EntityGraph.EntityGraphType.FETCH)
    fun findWithAllRelatedById(id: Long): Optional<FamilyGroup>
}