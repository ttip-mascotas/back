package org.pets.history.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty

@Entity
class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas un nombre")
    var name: String = ""

    @Column(length = 256, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas un mail")
    @Email
    var email: String = ""

    @Column(length = 256, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas una contraseña")
    var password: String = ""
}
