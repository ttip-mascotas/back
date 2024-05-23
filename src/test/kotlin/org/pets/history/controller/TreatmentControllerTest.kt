package org.pets.history.controller

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pets.history.domain.Pet
import org.pets.history.domain.Treatment
import org.pets.history.repository.PetRepository
import org.pets.history.repository.TreatmentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@SpringBootTest
@AutoConfigureMockMvc
class TreatmentControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var treatmentWithId: Treatment
    private lateinit var treatmentWithCalendar: Treatment
    private val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var treatmentRepository: TreatmentRepository

    @Autowired
    private lateinit var context: WebApplicationContext

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()

        val aPet = aPet()
        treatmentWithCalendar = aTreatment()
        aPet.startTreatment(treatmentWithCalendar)
        petRepository.save(aPet)
        treatmentWithId = treatmentRepository.save(treatmentWithCalendar)
    }

    @AfterEach
    fun tearDown() {
        petRepository.deleteAll()
    }

    fun aTreatment(): Treatment {
        return Treatment().apply {
            medicine = "Tramadol"
            datetime = LocalDateTime.of(LocalDateTime.now().plusYears(1).year, 3, 1, 0, 0, 0)
            dose = "1/4"
            frequency = 8
            numberOfTimes = 12
        }
    }

    fun aPet(): Pet {
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
        val id = treatmentWithId.id

        mockMvc.perform(
                MockMvcRequestBuilders.get("/treatments/$id")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.medicine").value(treatmentWithId.medicine))
                .andExpect(jsonPath("$.dose").value(treatmentWithId.dose))
                .andExpect(jsonPath("$.datetime").value(treatmentWithId.datetime.format(datetimeFormatter)))
                .andExpect(jsonPath("$.frequency").value(treatmentWithId.frequency))
                .andExpect(jsonPath("$.numberOfTimes").value(treatmentWithId.numberOfTimes))
                .andExpect(jsonPath("$.schedulesPerDay").isNotEmpty)
    }

    @Test
    fun `Given a treatment id that does not exist, fail to get said record and return an error`() {
        val id = 99

        mockMvc.perform(
                MockMvcRequestBuilders.get("/treatments/$id")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound)
    }
}