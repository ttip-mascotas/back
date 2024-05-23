package org.pets.history.controller

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer


@SpringBootTest
@ContextConfiguration(initializers = [IntegrationTest.Initializer::class])
@AutoConfigureMockMvc
abstract class IntegrationTest {
    companion object {
        val postgresContainer = PostgreSQLContainer<Nothing>("postgres:16.2-alpine3.19")
    }

    internal class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
            postgresContainer.start()

            TestPropertyValues.of(
                "spring.datasource.url=${postgresContainer.jdbcUrl}",
                "spring.datasource.username=${postgresContainer.username}",
                "spring.datasource.password=${postgresContainer.password}",
                "spring.datasource.driver-class-name=${postgresContainer.driverClassName}",
            ).applyTo(configurableApplicationContext.environment)
        }
    }
}
