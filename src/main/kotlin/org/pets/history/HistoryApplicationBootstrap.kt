package org.pets.history

import org.pets.history.domain.Pet
import org.pets.history.domain.PetSex
import org.pets.history.repository.PetRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
@Profile("!test")
class HistoryApplicationBootstrap(val resourceLoader: ResourceLoader, val petRepository: PetRepository) :
    InitializingBean {
    override fun afterPropertiesSet() {
        if (this.petRepository.count() > 0) {
            return
        }
        val pets = (1..5).map { i -> createPet(i) }
        this.petRepository.saveAll(pets)
    }

    private fun createPet(i: Int): Pet {
        return Pet().apply {
            name = "Pet$i"
            birthdate = LocalDate.of(2024, 1, 1).minusYears(i.toLong())
            sex = if (i % 2 == 0) PetSex.MALE else PetSex.FEMALE
            photo = encodeImageAsBase64(i)
            weight = (i + 1).toDouble()
            breed = "Breed$i"
        }
    }

    private fun encodeImageAsBase64(i: Int): String {
        val resource = resourceLoader.getResource("classpath:seed/$i.jpg")
        val image = resource.inputStream
        return Base64.getEncoder().encodeToString(image.readBytes())
    }
}
