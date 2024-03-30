package org.pets.history.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@SpringBootTest
@AutoConfigureMockMvc
class PetControllerTest {
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var context: WebApplicationContext

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    @DisplayName("responds with \"hello world\" when a request is made to fetch all the pets")
    fun respondsWhenARequestIdMade() {
        mockMvc.perform(
            get("/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json")
        ).andExpect(status().isOk)
            .andExpect(content().string("hello world"))
    }

    @Test
    fun `When searching for a pet by ID, the pet's information is obtained`() {
        mockMvc.perform(
                get("/pets/" + 1)
        )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.age").value("1"))
    }

    @Test
    fun `When searching for a pet by ID that does not exist, status not found is obtained`() {
        val id = 99

        mockMvc.perform(
                get("/pets/" + id)
        )
                .andExpect(status().isNotFound)
    }
}
