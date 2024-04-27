package org.pets.history.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity
class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @Column(length = 256, nullable = false)
    var url: String = ""

    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
}