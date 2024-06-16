package org.pets.history.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pets.history.domain.FamilyGroup
import org.pets.history.domain.Owner
import org.pets.history.domain.Pet
import org.pets.history.domain.PetSex
import org.pets.history.repository.FamilyGroupRepository
import org.pets.history.repository.OwnerRepository
import org.pets.history.repository.PetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate

@AutoConfigureMockMvc
class GroupControllerTest : IntegrationTest() {
    private val mapper = ObjectMapper()
    private lateinit var mockMvc: MockMvc
    private lateinit var group: FamilyGroup

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var ownerRepository: OwnerRepository

    @Autowired
    private lateinit var familyGroupRepository: FamilyGroupRepository

    @BeforeEach
    fun setUp() {
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()

        val users = mutableSetOf(
                Owner().apply {
                    name = "Ximena"
                    email = "ximena@example.com"
                    password = "passwordXimena1"
                },
                Owner().apply {
                    name = "Pablo"
                    email = "pablo@example.com"
                    password = "passwordPablo1"
                },
        )

        val initialPets = mutableSetOf(
                Pet().apply {
                    name = "Jake"
                    photo = "avatar"
                    weight = 5.5
                    birthdate = LocalDate.of(2023, 1, 1)
                    breed = "Shiba Inu"
                    fur = "Corto"
                    sex = PetSex.MALE
                },
                Pet().apply {
                    name = "Fiona"
                    photo = "avatar"
                    weight = 5.5
                    birthdate = LocalDate.of(2023, 1, 1)
                    breed = "Singapura"
                    fur = "Corto"
                    sex = PetSex.FEMALE
                },
        )

        val aGroup = FamilyGroup().apply {
            name = "Mis mascotas"
            members = users
            pets = initialPets
        }

        ownerRepository.saveAll(users)
        group = familyGroupRepository.save(aGroup)
        petRepository.saveAll(initialPets)
    }

    @AfterEach
    fun tearDown() {
        petRepository.deleteAll()
        familyGroupRepository.deleteAll()
        ownerRepository.deleteAll()
    }

    @Test
    fun `Given a group id that exists, the group's information is found and returned`() {

        val id = group.id

        mockMvc.get("/groups/$id") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { isNumber() }
            jsonPath("$.name") { value("Mis mascotas") }
            jsonPath("$.members.length()") { value(2) }
            jsonPath("$.pets.length()") { value(2) }
        }
    }

    @Test
    fun `Given a group id that does not exist, fail to get the information and return an error`() {
        val id = 99

        mockMvc.get("/groups/$id") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }
}