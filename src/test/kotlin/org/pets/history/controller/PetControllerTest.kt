package org.pets.history.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ninjasquad.springmockk.MockkBean
import io.minio.MinioClient
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pets.history.domain.MedicalVisit
import org.pets.history.domain.Pet
import org.pets.history.domain.Treatment
import org.pets.history.repository.PetRepository
import org.pets.history.service.MinioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTest {
    private val mapper = ObjectMapper()
    private val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var context: WebApplicationContext

    @MockkBean
    private lateinit var minioClient: MinioClient

    @MockkBean
    private lateinit var minioService: MinioService

    @Autowired
    private lateinit var petRepository: PetRepository

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @AfterEach
    fun tearDown() {
        petRepository.deleteAll()
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

    fun anyMedicalVisit(): MedicalVisit {
        return MedicalVisit().apply {
            address = "Evergreen 123"
            datetime = LocalDateTime.of(2024, 3, 28, 12, 0, 0)
            specialist = "Lisa Simpson"
            observations = "Healthy"
        }
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

    @Test
    fun `Retrieve all pets`() {
        petRepository.save(anyPet())
        petRepository.save(anyPet())

        mockMvc.perform(
            get("/pets")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.results.length()").value(2))
            .andExpect(jsonPath("$.results[0].id").isNumber)
            .andExpect(jsonPath("$.results[0].name").value("Pippin"))
            .andExpect(jsonPath("$.results[0].photo").isNotEmpty)
            .andExpect(jsonPath("$.results[0].birthdate").value("2023-01-01"))
            .andExpect(jsonPath("$.results[0].breed").value("San Bernardo"))
            .andExpect(jsonPath("$.results[0].weight").value(2.toDouble()))
            .andExpect(jsonPath("$.results[0].age").value("1"))
            .andExpect(jsonPath("$.results[0].fur").value("Long"))
            .andExpect(jsonPath("$.results[0].sex").value("FEMALE"))
    }

    @Test
    fun `Given a pet id that exists, the pet's information is found and returned`() {
        val pet = petRepository.save(anyPet())
        val id = pet.id

        mockMvc.perform(
            get("/pets/$id")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.name").value("Pippin"))
            .andExpect(jsonPath("$.photo").isNotEmpty)
            .andExpect(jsonPath("$.birthdate").value("2023-01-01"))
            .andExpect(jsonPath("$.breed").value("San Bernardo"))
            .andExpect(jsonPath("$.weight").value(2.toDouble()))
            .andExpect(jsonPath("$.age").value("1"))
            .andExpect(jsonPath("$.fur").value("Long"))
            .andExpect(jsonPath("$.sex").value("FEMALE"))
            .andExpect(jsonPath("$.medicalVisits").isEmpty())
    }

    @Test
    fun `Given a pet id that does not exists, an error is returned`() {
        val id = 99

        mockMvc.perform(
            get("/pets/$id")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `Given the details of a pet, registers said pet and return it`() {
        val pet = anyPet()
        val json = mapper.writeValueAsString(pet)

        mockMvc.perform(
            post("/pets")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.name").value(pet.name))
            .andExpect(jsonPath("$.photo").value(pet.photo))
            .andExpect(jsonPath("$.weight").value(pet.weight))
            .andExpect(jsonPath("$.birthdate").value(pet.birthdate.format(dateFormatter)))
            .andExpect(jsonPath("$.breed").value(pet.breed))
            .andExpect(jsonPath("$.fur").value(pet.fur))
            .andExpect(jsonPath("$.sex").value(pet.sex.toString()))
            .andExpect(jsonPath("$.age").value(pet.age))
    }

    @Test
    fun `Given the details of a pet, when said details contain a negative weight, fail to register the pet and return an error`() {
        val pet = anyPet()
        pet.weight = -1.0
        val json = mapper.writeValueAsString(pet)

        mockMvc.perform(
            post("/pets")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `Given a pet id that does exist and the details of a medical visit, registers said record and return it`() {
        val pet = petRepository.save(anyPet())
        val id = pet.id

        val medicalVisit = anyMedicalVisit()
        val json = mapper.writeValueAsString(medicalVisit)

        mockMvc.perform(
            post("/pets/$id/medical-records")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.address").value(medicalVisit.address))
            .andExpect(jsonPath("$.specialist").value(medicalVisit.specialist))
            .andExpect(jsonPath("$.datetime").value(medicalVisit.datetime.format(datetimeFormatter)))
            .andExpect(jsonPath("$.observations").value(medicalVisit.observations))
    }

    @Test
    fun `Given a pet id that does not exist and the details of a medical visit, fail to register said record and return an error`() {
        val id = 99

        val medicalVisit = anyMedicalVisit()
        val json = mapper.writeValueAsString(medicalVisit)

        mockMvc.perform(
            post("/pets/$id/medical-records")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `Given a pet id that exists, obtain all of its associated medical visits`() {
        var pet = anyPet()
        pet.addMedicalVisit(anyMedicalVisit())
        pet = petRepository.save(pet)
        val id = pet.id

        mockMvc.perform(
            get("/pets/$id/medical-records")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.results.length()").value(1))
            .andExpect(jsonPath("$.results.[0].id").isNumber)
            .andExpect(jsonPath("$.results.[0].address").value("Evergreen 123"))
            .andExpect(jsonPath("$.results.[0].datetime").value("2024-03-28T12:00:00"))
            .andExpect(jsonPath("$.results.[0].specialist").value("Lisa Simpson"))
            .andExpect(jsonPath("$.results.[0].observations").value("Healthy"))
    }

    @Test
    fun `Given a pet id that does exist and the details of a treatment, registers said record and return it`() {
        val pet = petRepository.save(anyPet())
        val id = pet.id

        val treatment = aTreatment()
        val json = mapper.writeValueAsString(treatment)

        mockMvc.perform(
            post("/pets/$id/treatments")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").isNumber)
            .andExpect(jsonPath("$.medicine").value(treatment.medicine))
            .andExpect(jsonPath("$.dose").value(treatment.dose))
            .andExpect(jsonPath("$.datetime").value(treatment.datetime.format(datetimeFormatter)))
            .andExpect(jsonPath("$.frequency").value(treatment.frequency))
            .andExpect(jsonPath("$.numberOfTimes").value(treatment.numberOfTimes))
    }

    @Test
    fun `Given a pet id that does not exist and the details of a treatment, fail to register said record and return an error`() {
        val id = 99

        val aTreatment = aTreatment()
        val json = mapper.writeValueAsString(aTreatment)

        mockMvc.perform(
            post("/pets/$id/treatments")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isNotFound)
    }

}
