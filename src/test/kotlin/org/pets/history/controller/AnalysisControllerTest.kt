package org.pets.history.controller

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.pets.history.domain.Analysis
import org.pets.history.domain.AnalysisImage
import org.pets.history.domain.Pet
import org.pets.history.repository.AnalysisRepository
import org.pets.history.repository.PetRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

class AnalysisControllerTest : IntegrationTest() {
    @Autowired
    private lateinit var petRepository: PetRepository

    @Autowired
    private lateinit var analysisRepository: AnalysisRepository

    private lateinit var analysis: Analysis

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

    fun anyAnalysis(): Analysis {
        val datetime = OffsetDateTime.of(2024, 3, 28, 12, 0, 0, 0, ZoneOffset.UTC)

        val analysisImage = AnalysisImage().apply {
            url = "https://a.fake.img.png"
            createdAt = datetime
        }
        val analysis = Analysis().apply {
            name = "test.pdf"
            size = 1024
            url = "https://a.fake.pdf.pdf"
            text = "a fake pdf"
            createdAt = datetime
        }
        analysis.addImage(analysisImage)
        return analysis
    }

    @BeforeEach
    fun setUp() {
        analysis = anyAnalysis()
        val pet = anyPet()
        pet.attachAnalysis(analysis)
        petRepository.save(pet)
        analysisRepository.save(analysis)
    }

    @AfterEach
    fun tearDown() {
        petRepository.deleteAll()
    }

    @Test
    fun `Given an analysis id that does exist, obtain the analysis with its images`() {
        mockMvc.get("/analyses/${analysis.id}") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.name") { value(analysis.name) }
            jsonPath("$.size") { value(analysis.size) }
            jsonPath("$.url") { value(analysis.url) }
            jsonPath("$.createdAt") { isNotEmpty() }
            jsonPath("$.images.size()") { value(analysis.images.size) }
            jsonPath("$.images[0].url") { value(analysis.images.first().url) }
            jsonPath("$.images[0].createdAt") { isNotEmpty() }
        }
    }

    @Test
    fun `Given an analysis id that does not exist, fail to get said record and return an error`() {
        val id = 99

        mockMvc.get("/analyses/$id") {
            accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }
}
