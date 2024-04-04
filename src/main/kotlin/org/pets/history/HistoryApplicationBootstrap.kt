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
        if (petRepository.count() > 0) {
            return
        }

        val petSeeds = listOf(
            Pet().apply {
                name = "Jake"
                photo = encodeImageAsBase64(0)
                weight = 5.5
                birthdate = LocalDate.of(2023, 1, 1)
                breed = "Shiba Inu"
                fur = "Corto"
                sex = PetSex.MALE
            },
            Pet().apply {
                name = "Fiona"
                photo = encodeImageAsBase64(1)
                weight = 5.5
                birthdate = LocalDate.of(2023, 1, 1)
                breed = "Singapura"
                fur = "Corto"
                sex = PetSex.FEMALE
            },
            Pet().apply {
                name = "Braulio"
                photo = encodeImageAsBase64(2)
                weight = 0.25
                birthdate = LocalDate.of(2023, 1, 1)
                breed = "Hámster Sirio"
                fur = "Corto"
                sex = PetSex.MALE
            },
            Pet().apply {
                name = "Verdún"
                photo = encodeImageAsBase64(3)
                weight = 0.15
                birthdate = LocalDate.of(2023, 1, 1)
                breed = "Gecko Leopardo"
                fur = "Escamas verdes con puntos rojos"
                sex = PetSex.MALE
            },
            Pet().apply {
                name = "Naranjita"
                photo = encodeImageAsBase64(4)
                weight = 1.8
                birthdate = LocalDate.of(2023, 1, 1)
                breed = "Gallina Rhode Island Red"
                fur = "Plumas anaranjas"
                sex = PetSex.FEMALE
            },
        )
        petRepository.saveAll(petSeeds)
    }

    private fun encodeImageAsBase64(i: Int): String {
        val resource = resourceLoader.getResource("classpath:seed/$i.jpg")
        val image = resource.inputStream
        return Base64.getEncoder().encodeToString(image.readBytes())
    }
}
