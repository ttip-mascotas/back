package org.pets.history.controller

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pets.history.domain.FamilyGroup
import org.pets.history.domain.Owner
import org.pets.history.repository.FamilyGroupRepository
import org.pets.history.repository.OwnerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

class UserControllerTest : IntegrationTest() {
    @Autowired
    private lateinit var ownerRepository: OwnerRepository

    @Autowired
    private lateinit var familyGroupRepository: FamilyGroupRepository

    private lateinit var user: Owner

    @BeforeEach
    fun setUp() {
        val anUser = Owner().apply {
            name = "Ximena"
            email = "ximena@example.com"
            password = "passwordXimena1"
        }

        val groups = mutableSetOf(
            FamilyGroup().apply {
                name = "Mis mascotas"
                members = mutableSetOf(anUser)
                pets = mutableSetOf()
            },
            FamilyGroup().apply {
                name = "Otro grupo"
                members = mutableSetOf(anUser)
                pets = mutableSetOf()
            }
        )

        user = ownerRepository.save(anUser)
        familyGroupRepository.saveAll(groups)
    }

    @AfterEach
    fun tearDown() {
        familyGroupRepository.deleteAll()
        ownerRepository.deleteAll()
    }

    @Test
    fun `Retrieve all groups for a user`() {
        val id = user.id

        mockMvc.get("/users/$id/groups") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.results.length()") { value(2) }
            jsonPath("$.results[0].id") { isNumber() }
            jsonPath("$.results[0].name") { value("Mis mascotas") }
            jsonPath("$.results[1].id") { isNumber() }
            jsonPath("$.results[1].name") { value("Otro grupo") }
        }
    }
}
