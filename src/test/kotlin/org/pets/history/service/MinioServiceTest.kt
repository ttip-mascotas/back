package org.pets.history.service

import com.ninjasquad.springmockk.MockkBean
import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.util.*


@SpringBootTest
class MinioServiceTest {
    @MockkBean
    private lateinit var minioClient: MinioClient

    @MockK
    private lateinit var putObjectResult: ObjectWriteResponse

    @Autowired
    private lateinit var service: MinioService

    @BeforeEach
    fun setUp() {
        every { minioClient.bucketExists(any()) } returns false
        justRun { minioClient.makeBucket(any()) }
        every { minioClient.putObject(any()) } returns putObjectResult
    }

    @ParameterizedTest
    @MethodSource("avatarProvider")
    fun `Uploads an avatar successfully`(bucket: String, fileExtension: String, mediaType: String) {
        val filename = UUID.randomUUID().toString()

        every { putObjectResult.bucket() } returns bucket
        every { putObjectResult.`object`() } returns filename

        val file = MockMultipartFile(
            "avatar",
            "avatar.$fileExtension",
            mediaType,
            "an_image".toByteArray()
        )

        val avatarURL = service.uploadAvatar(file.inputStream, file.contentType!!)

        assertEquals("http://127.0.0.1:9000/$bucket/$filename", avatarURL)

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
    fun `Fails to upload an avatar`() {
        val filename = UUID.randomUUID().toString()
        val bucket = "public"
        val mediaType = MediaType.APPLICATION_PROBLEM_XML_VALUE
        val fileExtension = "jpeg"

        every { putObjectResult.bucket() } returns bucket
        every { putObjectResult.`object`() } returns filename

        val file = MockMultipartFile(
            "avatar",
            "avatar.$fileExtension",
            mediaType,
            "an_ivalid_file".toByteArray()
        )

        assertThrows<MediaTypeNotValidException> {
            service.uploadAvatar(
                file.inputStream,
                file.contentType!!
            )
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
    fun `Uploads a pet analysis successfully`() {
        val filename = UUID.randomUUID().toString()
        val petId = 1L
        val bucket = "analysis"
        val mediaType = MediaType.APPLICATION_PDF_VALUE
        val fileExtension = "pdf"

        every { putObjectResult.bucket() } returns bucket
        every { putObjectResult.`object`() } returns "$petId/$filename"

        val file = MockMultipartFile(
            "avatar",
            "avatar.$fileExtension",
            mediaType,
            "a_pdf_file".toByteArray()
        )

        val analysisURL = service.uploadPetAnalysis(petId, file.inputStream, file.contentType!!)

        assertEquals("http://127.0.0.1:9000/$bucket/$petId/$filename", analysisURL)

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
    fun `Fails to upload a pet analysis`() {
        val filename = UUID.randomUUID().toString()
        val petId = 1L
        val bucket = "analysis"
        val mediaType = MediaType.APPLICATION_PROBLEM_XML_VALUE
        val fileExtension = "pdf"

        every { putObjectResult.bucket() } returns bucket
        every { putObjectResult.`object`() } returns "$petId/$filename"

        val file = MockMultipartFile(
            "avatar",
            "avatar.$fileExtension",
            mediaType,
            "an_ivalid_file".toByteArray()
        )

        assertThrows<MediaTypeNotValidException> {
            service.uploadPetAnalysis(
                petId,
                file.inputStream,
                file.contentType!!
            )
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

    companion object {
        @JvmStatic
        fun avatarProvider(): List<Arguments> = listOf(
            Arguments.of("public", "jpg", MediaType.IMAGE_JPEG_VALUE),
            Arguments.of("public", "png", MediaType.IMAGE_PNG_VALUE),
        )
    }
}
