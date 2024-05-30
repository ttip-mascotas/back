package org.pets.history.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pets.history.domain.Pet
import org.pets.history.domain.Treatment
import org.pets.history.repository.PetRepository
import org.pets.history.repository.TreatmentRepository
import org.pets.history.serializer.TreatmentLogUpdateDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AutoConfigureMockMvc
class TreatmentControllerTest : IntegrationTest() {
    private val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val mapper = ObjectMapper()
    private lateinit var mockMvc: MockMvc
    private lateinit var treatment: Treatment

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var treatmentRepository: TreatmentRepository

    @BeforeEach
    fun setUp() {
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()

        treatment = anyTreatment()
        val pet = anyPet()
        pet.startTreatment(treatment)
        petRepository.save(pet)
        treatmentRepository.save(treatment)
        println("h")
    }

    @AfterEach
    fun tearDown() {
        petRepository.deleteAll()
    }

    fun anyTreatment(): Treatment {
        return Treatment().apply {
            medicine = "Tramadol"
            datetime = LocalDateTime.of(LocalDateTime.now().plusYears(1).year, 3, 1, 0, 0, 0)
            dose = "1/4"
            frequency = 8
            numberOfTimes = 12
        }
    }

    fun anyPet(): Pet {
        return Pet().apply {
            name = "Pippin"
            photo = "https://a.fake.img.png"
            weight = 2.0
            birthdate = LocalDate.of(2023, 1, 1)
            breed = "San Bernardo"
            fur = "Long"
        }
    }

    @Test
    fun `Given a treatment id that does exist, obtain the treatment with his calendar`() {
        mockMvc.get("/treatments/${treatment.id}") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.medicine") { value(treatment.medicine) }
            jsonPath("$.dose") { value(treatment.dose) }
            jsonPath("$.datetime") { value(treatment.datetime.format(datetimeFormatter)) }
            jsonPath("$.frequency") { value(treatment.frequency) }
            jsonPath("$.numberOfTimes") { value(treatment.numberOfTimes) }
            jsonPath("$.logs.length()") { value(treatment.numberOfTimes) }
        }
    }

    @Test
    fun `Given a treatment id that does not exist, fail to get said record and return an error`() {
        val id = 99

        mockMvc.get("/treatments/$id") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `Given a treatment id, a treatment log id that do exist and a payload, update the log with the values provided by the payload`() {
        val treatmentLog = treatment.logs.first()
        val treatmentLogUpdateDTO = TreatmentLogUpdateDTO(administered = true)

        mockMvc.put("/treatments/${treatment.id}/logs/${treatmentLog.id}") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(treatmentLogUpdateDTO)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(treatmentLog.id) }
            jsonPath("$.administered") { value(!treatmentLog.administered) }
            jsonPath("$.datetime") { value(treatmentLog.datetime.format(datetimeFormatter)) }
        }
    }
}
