package org.pets.history.controller

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
abstract class IntegrationTest {
    companion object {
        @Container
        @ServiceConnection
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:16.2-alpine3.19")
    }
}
