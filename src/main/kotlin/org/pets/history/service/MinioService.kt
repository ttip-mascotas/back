package org.pets.history.service

import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.pets.history.configuration.properties.MinioProperties
import org.springframework.stereotype.Component
import java.io.InputStream
import java.util.*

@Component
class MinioService(private val minioClient: MinioClient, private val minioProperties: MinioProperties) {
    fun uploadFile(stream: InputStream, contentType: String): String {
        val putObjectArgs = PutObjectArgs.builder()
            .bucket(minioProperties.publicBucket)
            .`object`(UUID.randomUUID().toString())
            .contentType(contentType)
            .stream(stream, -1, minioProperties.putObjectPartSize)
            .build()
        val result = minioClient.putObject(putObjectArgs)
        return "${minioProperties.host}/${result.bucket()}/${result.`object`()}"
    }
}

// IllegalArgumentException
// io. minio. errors. MinioException
