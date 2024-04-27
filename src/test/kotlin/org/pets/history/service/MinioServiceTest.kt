package org.pets.history.service

import com.ninjasquad.springmockk.MockkBean
import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import java.util.*


@SpringBootTest
class MinioServiceTest {
    @MockkBean
    private lateinit var minioClient: MinioClient

    @Autowired
    private lateinit var service: MinioService

    @Test
    fun `Uploads a JPG avatar successfully`() {
        val filename = UUID.randomUUID().toString()
        val bucket = "public"
        val putObjectResult = mockk<ObjectWriteResponse>()

        every { putObjectResult.bucket() } returns bucket
        every { putObjectResult.`object`() } returns filename
        every { minioClient.putObject(any()) } returns putObjectResult
        every { minioClient.bucketExists(any()) } returns true

        val file = MockMultipartFile(
            "avatar",
            "avatar.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "an_image".toByteArray()
        )

        val avatarURL = service.uploadAvatar(file.inputStream, file.contentType!!)

        assertEquals("http://127.0.0.1:9000/$bucket/$filename", avatarURL)

        verify { minioClient.putObject(any()) }
        verify { minioClient.bucketExists(any()) }

        confirmVerified(minioClient)
    }
}
