package org.pets.history.serializer

import org.pets.history.domain.Pet
import java.time.LocalDate

data class PetDTO(
    val id: Long,
    val name: String,
    val photo: String,
    val age: Int,
    val weight: Double,
    val birthdate: LocalDate,
    val breed: String,
    val fur: String,
    val sex: String,
) {
    companion object {
        fun fromPet(pet: Pet): PetDTO =
            PetDTO(
                id = pet.id!!,
                name = pet.name,
                photo = pet.photo,
                age = pet.age(),
                weight = pet.weight,
                birthdate = pet.birthdate,
                breed = pet.breed,
                fur = pet.fur,
                sex = pet.sex.toString(),
            )
    }
}
