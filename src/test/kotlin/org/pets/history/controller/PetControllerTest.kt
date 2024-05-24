package org.pets.history.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ninjasquad.springmockk.MockkBean
import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pets.history.domain.MedicalVisit
import org.pets.history.domain.Pet
import org.pets.history.domain.Treatment
import org.pets.history.repository.PetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AutoConfigureMockMvc
class PetControllerTest : IntegrationTest() {
    private val datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val mapper = ObjectMapper()
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var context: WebApplicationContext

    @MockkBean
    private lateinit var minioClient: MinioClient

    @MockK
    private lateinit var putObjectResult: ObjectWriteResponse

    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var resourceLoader: ResourceLoader

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        every { minioClient.bucketExists(any()) } returns false
        justRun { minioClient.makeBucket(any()) }
        every { minioClient.putObject(any()) } returns putObjectResult
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

    fun anyTreatment(): Treatment {
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

        mockMvc.get("/pets") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.results.length()") { value(2) }
            jsonPath("$.results[0].id") { isNumber() }
            jsonPath("$.results[0].name") { value("Pippin") }
            jsonPath("$.results[0].photo") { isNotEmpty() }
            jsonPath("$.results[0].birthdate") { value("2023-01-01") }
            jsonPath("$.results[0].breed") { value("San Bernardo") }
            jsonPath("$.results[0].weight") { value(2.toDouble()) }
            jsonPath("$.results[0].age") { value("1") }
            jsonPath("$.results[0].sex") { value("FEMALE") }
        }
    }

    @Test
    fun `Given a pet id that exists, the pet's information is found and returned`() {
        val pet = petRepository.save(anyPet())
        val id = pet.id

        mockMvc.get("/pets/$id") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { isNumber() }
            jsonPath("$.name") { value("Pippin") }
            jsonPath("$.photo") { isNotEmpty() }
            jsonPath("$.birthdate") { value("2023-01-01") }
            jsonPath("$.breed") { value("San Bernardo") }
            jsonPath("$.weight") { value(2.toDouble()) }
            jsonPath("$.age") { value("1") }
            jsonPath("$.fur") { value("Long") }
            jsonPath("$.sex") { value("FEMALE") }
            jsonPath("$.medicalVisits") { isEmpty() }
        }
    }

    @Test
    fun `Given a pet id that does not exists, an error is returned`() {
        val id = 99

        mockMvc.get("/pets/$id") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `Given the details of a pet, registers said pet and return it`() {
        val pet = anyPet()

        mockMvc.post("/pets") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(pet)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { isNumber() }
            jsonPath("$.name") { value(pet.name) }
            jsonPath("$.photo") { value(pet.photo) }
            jsonPath("$.weight") { value(pet.weight) }
            jsonPath("$.birthdate") { value(pet.birthdate.format(dateFormatter)) }
            jsonPath("$.breed") { value(pet.breed) }
            jsonPath("$.fur") { value(pet.fur) }
            jsonPath("$.sex") { value(pet.sex.toString()) }
            jsonPath("$.age") { value(pet.age) }
        }
    }

    @Test
    fun `Given the details of a pet, when said details contain a negative weight, fail to register the pet and return an error`() {
        val pet = anyPet()
        pet.weight = -1.0

        mockMvc.post("/pets") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(pet)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `Given a pet id that does exist and the details of a medical visit, registers said record and return it`() {
        val pet = petRepository.save(anyPet())
        val id = pet.id
        val medicalVisit = anyMedicalVisit()

        mockMvc.post("/pets/$id/medical-records") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(medicalVisit)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { isNumber() }
            jsonPath("$.address") { value(medicalVisit.address) }
            jsonPath("$.specialist") { value(medicalVisit.specialist) }
            jsonPath("$.datetime") { value(medicalVisit.datetime.format(datetimeFormatter)) }
            jsonPath("$.observations") { value(medicalVisit.observations) }
        }
    }

    @Test
    fun `Given a pet id that does not exist and the details of a medical visit, fail to register said record and return an error`() {
        val id = 99
        val medicalVisit = anyMedicalVisit()

        mockMvc.post("/pets/$id/medical-records") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(medicalVisit)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `Given a pet id that exists, obtain all of its associated medical visits`() {
        var pet = anyPet()
        pet.addMedicalVisit(anyMedicalVisit())
        pet = petRepository.save(pet)
        val id = pet.id

        mockMvc.get("/pets/$id/medical-records") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.results.length()") { value(1) }
            jsonPath("$.results.[0].id") { isNumber() }
            jsonPath("$.results.[0].address") { value("Evergreen 123") }
            jsonPath("$.results.[0].datetime") { value("2024-03-28T12:00:00") }
            jsonPath("$.results.[0].specialist") { value("Lisa Simpson") }
            jsonPath("$.results.[0].observations") { value("Healthy") }
        }
    }

    @Test
    fun `Given a pet id that does exist and the details of a treatment, registers said record and return it`() {
        val pet = petRepository.save(anyPet())
        val id = pet.id
        val treatment = anyTreatment()

        mockMvc.post("/pets/$id/treatments") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(treatment)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { isNumber() }
            jsonPath("$.medicine") { value(treatment.medicine) }
            jsonPath("$.dose") { value(treatment.dose) }
            jsonPath("$.datetime") { value(treatment.datetime.format(datetimeFormatter)) }
            jsonPath("$.frequency") { value(treatment.frequency) }
            jsonPath("$.numberOfTimes") { value(treatment.numberOfTimes) }
        }
    }

    @Test
    fun `Given a pet id that does not exist and the details of a treatment, fail to register said record and return an error`() {
        val id = 99
        val treatment = anyTreatment()

        mockMvc.post("/pets/$id/treatments") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(treatment)
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `Given an avatar, uploads it successfully`() {
        val imageFile = MockMultipartFile(
            "avatar",
            "avatar.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "an_image".toByteArray()
        )
        val bucket = "public"
        val filename = UUID.randomUUID().toString()

        every { putObjectResult.bucket() } returns bucket
        every { putObjectResult.`object`() } returns filename

        val avatarURL = "http://127.0.0.1:9000/$bucket/$filename"

        mockMvc.multipart("/pets/avatars") {
            file(imageFile)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.url") { value(avatarURL) }
        }

        verify {
            minioClient.bucketExists(any())
            minioClient.makeBucket(any())
            minioClient.putObject(any())
            putObjectResult.bucket()
            putObjectResult.`object`()
        }

        confirmVerified(minioClient)
        confirmVerified(putObjectResult)
    }

    @Test
    fun `Given an avatar that is not an image, the upload fails`() {
        val imageFile = MockMultipartFile(
            "avatar",
            "avatar.xml",
            MediaType.APPLICATION_XML_VALUE,
            "an_xml".toByteArray()
        )

        mockMvc.multipart("/pets/avatars") {
            file(imageFile)
        }.andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }

        verify(exactly = 0) {
            minioClient.bucketExists(any())
            minioClient.makeBucket(any())
            minioClient.putObject(any())
            putObjectResult.bucket()
            putObjectResult.`object`()
        }

        confirmVerified(minioClient)
        confirmVerified(putObjectResult)
    }

    @Test
    fun `Given a pet id and an analysis file, uploads it successfully`() {
        val pet = petRepository.save(anyPet())
        val resource = resourceLoader.getResource("classpath:test-data/dummy.pdf")

        val multipartFile = MockMultipartFile(
            "analysis",
            resource.filename,
            MediaType.APPLICATION_PDF_VALUE,
            resource.inputStream.readAllBytes()
        )
        val bucket = "analyses"
        val filename = UUID.randomUUID().toString()

        every { putObjectResult.bucket() } returns bucket
        every { putObjectResult.`object`() } returns "${pet.id}/$filename"

        mockMvc.multipart("/pets/${pet.id}/analyses") {
            file(multipartFile)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { isNumber() }
            jsonPath("$.name") { value(resource.filename) }
            jsonPath("$.size") { value(13264) }
            jsonPath("$.createdAt") { isNotEmpty() }
        }

        verify {
            minioClient.bucketExists(any())
            minioClient.makeBucket(any())
            minioClient.putObject(any())
            putObjectResult.bucket()
            putObjectResult.`object`()
        }

        confirmVerified(minioClient)
        confirmVerified(putObjectResult)
    }

    @Test
    fun `Given a pet id and an analysis file, when the analysis file has no original file name, uploads it successfully with a default name`() {
        val pet = petRepository.save(anyPet())
        val resource = resourceLoader.getResource("classpath:test-data/dummy.pdf")

        val multipartFile = MockMultipartFile(
            "analysis",
            null,
            MediaType.APPLICATION_PDF_VALUE,
            resource.inputStream.readAllBytes()
        )
        val bucket = "analyses"
        val filename = UUID.randomUUID().toString()

        every { putObjectResult.bucket() } returns bucket
        every { putObjectResult.`object`() } returns "${pet.id}/$filename"

        mockMvc.multipart("/pets/${pet.id}/analyses") {
            file(multipartFile)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { isNumber() }
            jsonPath("$.name") { value("análisis.pdf") }
            jsonPath("$.size") { value(13264) }
            jsonPath("$.createdAt") { isNotEmpty() }
        }

        verify {
            minioClient.bucketExists(any())
            minioClient.makeBucket(any())
            minioClient.putObject(any())
            putObjectResult.bucket()
            putObjectResult.`object`()
        }

        confirmVerified(minioClient)
        confirmVerified(putObjectResult)
    }

    @Test
    fun `Given a pet id and an analysis file that is not a PDF, the upload fails`() {
        val pet = petRepository.save(anyPet())

        val multipartFile = MockMultipartFile(
            "analysis",
            "pet_analysis.xml",
            MediaType.APPLICATION_XML_VALUE,
            "an_xml".toByteArray()
        )

        mockMvc.multipart("/pets/${pet.id}/analyses") {
            file(multipartFile)
        }.andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }

        verify(exactly = 0) {
            minioClient.bucketExists(any())
            minioClient.makeBucket(any())
            minioClient.putObject(any())
            putObjectResult.bucket()
            putObjectResult.`object`()
        }

        confirmVerified(minioClient)
        confirmVerified(putObjectResult)
    }

    @Test
    fun `Given a pet id and search criteria, return a single file that matches the criteria`() {
        val pet = petRepository.save(anyPet())
        val resource = resourceLoader.getResource("classpath:test-data/dummy.pdf")

        val multipartFile = MockMultipartFile(
            "analysis",
            resource.filename,
            MediaType.APPLICATION_PDF_VALUE,
            resource.inputStream.readAllBytes()
        )
        val bucket = "analyses"
        val filename = UUID.randomUUID().toString()

        every { putObjectResult.bucket() } returns bucket
        every { putObjectResult.`object`() } returns "${pet.id}/$filename"

        mockMvc.multipart("/pets/${pet.id}/analyses") {
            file(multipartFile)
        }.andExpect {
            status { isCreated() }
            content { contentType(MediaType.APPLICATION_JSON) }
        }

        verify {
            minioClient.bucketExists(any())
            minioClient.makeBucket(any())
            minioClient.putObject(any())
            putObjectResult.bucket()
            putObjectResult.`object`()
        }

        confirmVerified(minioClient)
        confirmVerified(putObjectResult)

        mockMvc.get("/pets/${pet.id}/analyses?q=dummy") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.results.length()") { value(1) }
            jsonPath("$.results[0].id") { isNumber() }
            jsonPath("$.results[0].name") { value(resource.filename) }
            jsonPath("$.results[0].size") { value(13264) }
            jsonPath("$.results[0].createdAt") { isNotEmpty() }
        }
    }

}
