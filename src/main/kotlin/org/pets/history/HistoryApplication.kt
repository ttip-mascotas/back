package org.pets.history

import org.pets.history.configuration.properties.MinioProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(MinioProperties::class)
class HistoryApplication

fun main(args: Array<String>) {
    runApplication<HistoryApplication>(*args)
}
