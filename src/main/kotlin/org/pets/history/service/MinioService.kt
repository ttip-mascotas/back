package org.pets.history.service

import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.pets.history.configuration.properties.MinioProperties
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import java.io.InputStream
import java.util.*

@Component
class MinioService(private val minioClient: MinioClient, private val minioProperties: MinioProperties) {
    private val supportedAvatarContentTypes = setOf(MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE)
    private val supportedAnalysisContentTypes = setOf(MediaType.APPLICATION_PDF_VALUE)

    fun uploadAvatar(stream: InputStream, contentType: String): String {
        if (!supportedAvatarContentTypes.contains(contentType)) {
            throw MediaTypeNotValidException(contentType, supportedAvatarContentTypes)
        }
        return uploadFileToBucket(minioProperties.publicBucket, generateUniqueFilename(), stream, contentType)
    }

    fun uploadPetAnalysis(petId: Long, stream: InputStream, contentType: String): String {
        if (!supportedAnalysisContentTypes.contains(contentType)) {
            throw MediaTypeNotValidException(contentType, supportedAnalysisContentTypes)
        }
        return uploadFileToBucket(
            minioProperties.analysisBucket,
            "$petId/${generateUniqueFilename()}",
            stream,
            contentType
        )
    }

    private fun generateUniqueFilename(): String {
        return UUID.randomUUID().toString()
    }

    private fun uploadFileToBucket(bucket: String, fileName: String, stream: InputStream, contentType: String): String {
        createBucket(bucket)
        val putObjectArgs = PutObjectArgs.builder()
            .bucket(bucket)
            .`object`(fileName)
            .contentType(contentType)
            .stream(stream, -1, minioProperties.putObjectPartSize)
            .build()
        val result = minioClient.putObject(putObjectArgs)
        return "${minioProperties.host}/${result.bucket()}/${result.`object`()}"
    }

    private fun createBucket(bucket: String) {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build())
        }
    }
}
