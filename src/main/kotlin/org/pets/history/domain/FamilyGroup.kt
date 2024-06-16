package org.pets.history.domain
import com.fasterxml.jackson.annotation.JsonView
import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import org.pets.history.serializer.View

@Entity
@NamedEntityGraphs(
    NamedEntityGraph(
        name = "joinGroupWithAll", attributeNodes = [
            NamedAttributeNode("members"),
            NamedAttributeNode("pets"),
        ]
    )
)
class FamilyGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(length = 128, nullable = false)
    @NotEmpty(message = "Es necesario que introduzcas un nombre")
    var name: String = ""

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonView(View.ExtendedFamilyGroup::class)
    var members: MutableSet<Owner> = mutableSetOf()

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "family_group_id")
    @JsonView(View.ExtendedFamilyGroup::class)
    var pets: MutableSet<Pet> = mutableSetOf()
}