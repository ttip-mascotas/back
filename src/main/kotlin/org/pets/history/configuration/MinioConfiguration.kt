package org.pets.history.configuration

import io.minio.MinioClient
import org.pets.history.configuration.properties.MinioProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MinioConfiguration(private val minioProperties: MinioProperties) {
    @Bean
    fun minioClient(): MinioClient {
        val client = MinioClient.builder()
            .endpoint(minioProperties.host)
            .credentials(minioProperties.username, minioProperties.password)
            .build()
        return client
    }
}
