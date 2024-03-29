package org.pets.history.serializer

import org.pets.history.domain.Pet

class PetDTO(pet: Pet) {
    var id = pet.id
    var name = pet.name
    var photo = pet.photo
    var age = pet.age()
    var weight = pet.weight
    var birthdate = pet.birthdate
    var breed = pet.breed
    var fur = pet.fur
    var sex = pet.sex
}