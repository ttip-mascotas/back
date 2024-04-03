package org.pets.history.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pets.history.domain.MedicalVisit
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTest(private val context: WebApplicationContext) {
    private val mapper = ObjectMapper()
    private val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()

        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    fun anyMedicalVisit(): MedicalVisit {
        return MedicalVisit().apply {
            address = "Evergreen 123"
            datetime = LocalDateTime.of(2024, 3, 28, 12, 0, 0)
            specialist = "Lisa Simpson"
            observations = "Healthy"
        }
    }

    @Test
    fun `While searching for a pet by ID, the pet's information is obtained`() {
        val id = 1

        mockMvc.perform(
            get("/pets/$id")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value("Pet1"))
            .andExpect(jsonPath("$.birthdate").value("2023-01-01"))
            .andExpect(jsonPath("$.breed").value("Breed$id"))
            .andExpect(jsonPath("$.weight").value(2.toDouble()))
            .andExpect(jsonPath("$.age").value("1"))
            .andExpect(jsonPath("$.fur").value(""))
            .andExpect(jsonPath("$.sex").value("FEMALE"))
            .andExpect(jsonPath("$.medicalVisits").isEmpty())
    }

    @Test
    fun `While searching for a pet by ID that does not exist, status not found is obtained`() {
        val id = 99

        mockMvc.perform(
            get("/pets/$id")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").value("Pet with $id does not exist"))
    }

    @Test
    fun `While registering a medical visit for a pet by ID that does exist, the medical visit details are obtained`() {
        val id = 1
        val medicalVisit = anyMedicalVisit()
        val json = mapper.writeValueAsString(medicalVisit)

        mockMvc.perform(
            post("/pets/$id/medical-records")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.address").value(medicalVisit.address))
            .andExpect(jsonPath("$.specialist").value(medicalVisit.specialist))
            .andExpect(jsonPath("$.datetime").value(medicalVisit.datetime.format(datetimeFormatter)))
            .andExpect(jsonPath("$.observations").value(medicalVisit.observations))
    }

}
