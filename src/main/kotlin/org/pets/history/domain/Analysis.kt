package org.pets.history.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import org.hibernate.annotations.CreationTimestamp
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity
class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(length = 256, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas una nombre de archivo")
    var name: String = ""

    @Column(nullable = false)
    var size: Long = 0

    @Column(length = 256, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas una ruta de archivo")
    @JsonIgnore
    var url: String = ""

    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    var text: String = ""

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
}
