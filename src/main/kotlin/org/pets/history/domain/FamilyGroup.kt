package org.pets.history.domain
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty

@Entity
class FamilyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas un nombre")
    var name: String = ""

    @ManyToMany(fetch = FetchType.LAZY)
    var members: MutableSet<Owner> = mutableSetOf()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "family_group_id")
    var pets: MutableSet<Pet> = mutableSetOf()
}