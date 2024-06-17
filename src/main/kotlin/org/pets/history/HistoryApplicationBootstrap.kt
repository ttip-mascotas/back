package org.pets.history

import org.pets.history.domain.FamilyGroup
import org.pets.history.domain.Pet
import org.pets.history.domain.PetSex
import org.pets.history.domain.Owner
import org.pets.history.repository.FamilyGroupRepository
import org.pets.history.repository.PetRepository
import org.pets.history.repository.OwnerRepository
import org.pets.history.service.MinioService
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ResourceLoader
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Profile("!test")
class HistoryApplicationBootstrap(
        val resourceLoader: ResourceLoader,
        val petRepository: PetRepository,
        val ownerRepository: OwnerRepository,
        val familyGroupRepository: FamilyGroupRepository,
        val minioService: MinioService,
) :
    InitializingBean {
    override fun afterPropertiesSet() {
        if (petRepository.count() > 0) {
            return
        }

        val usersSeeds = mutableSetOf(
                Owner().apply {
                    name = "Ximena"
                    email = "ximena@example.com"
                    password = "passwordXimena1"
                },
                Owner().apply {
                    name = "Pablo"
                    email = "pablo@example.com"
                    password = "passwordPablo1"
                },
        )

        val petSeeds = mutableSetOf(
            Pet().apply {
                name = "Jake"
                photo = getAvatarURL(0)
                weight = 5.5
                birthdate = LocalDate.of(2023, 1, 1)
                breed = "Shiba Inu"
                fur = "Corto"
                sex = PetSex.MALE
            },
            Pet().apply {
                name = "Fiona"
                photo = getAvatarURL(1)
                weight = 5.5
                birthdate = LocalDate.of(2023, 1, 1)
                breed = "Singapura"
                fur = "Corto"
                sex = PetSex.FEMALE
            },
            Pet().apply {
                name = "Braulio"
                photo = getAvatarURL(2)
                weight = 0.25
                birthdate = LocalDate.of(2023, 1, 1)
                breed = "Hámster Sirio"
                fur = "Corto"
                sex = PetSex.MALE
            },
            Pet().apply {
                name = "Verdún"
                photo = getAvatarURL(3)
                weight = 0.15
                birthdate = LocalDate.of(2023, 1, 1)
                breed = "Gecko Leopardo"
                fur = "Escamas verdes con puntos rojos"
                sex = PetSex.MALE
            },
            Pet().apply {
                name = "Naranjita"
                photo = getAvatarURL(4)
                weight = 1.8
                birthdate = LocalDate.of(2023, 1, 1)
                breed = "Gallina Rhode Island Red"
                fur = "Plumas anaranjas"
                sex = PetSex.FEMALE
            },
        )

        val group = FamilyGroup().apply {
            name = "Mis mascotas"
            members = usersSeeds
            pets = petSeeds
        }

        ownerRepository.saveAll(usersSeeds)
        familyGroupRepository.save(group)
        petRepository.saveAll(petSeeds)
    }

    private fun getAvatarURL(i: Int): String {
        val resource = resourceLoader.getResource("classpath:seed/$i.jpg")
        return minioService.uploadAvatar(resource.inputStream, MediaType.IMAGE_JPEG_VALUE)
    }
}
