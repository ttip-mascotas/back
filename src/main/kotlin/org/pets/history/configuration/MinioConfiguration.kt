package org.pets.history.configuration

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
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
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.publicBucket).build())) {
            client.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.publicBucket).build())
        }
        return client
    }
}
