package org.pets.history.configuration.properties

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "minio")
@ConfigurationPropertiesBinding
data class MinioProperties(
    @URL(protocol = "http")
    @Value("\${host}")
    val host: String,

    @NotBlank
    @Value("\${username}")
    val username: String,

    @NotBlank
    @Value("\${password}")
    val password: String,

    @NotBlank
    @Value("\${public_bucket}")
    val publicBucket: String,

    @NotBlank
    @Value("\${analysis_bucket}")
    val analysisBucket: String,

    @Max(value = 15728640)
    @Value("\${put_object_part_size}")
    val putObjectPartSize: Long,
)
