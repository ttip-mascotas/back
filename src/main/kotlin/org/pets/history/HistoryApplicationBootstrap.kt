package org.pets.history

import org.pets.history.domain.Pet
import org.pets.history.domain.PetSex
import org.pets.history.repository.PetRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class HistoryApplicationBootstrap(
    val petRepository: PetRepository
) : InitializingBean {

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
            birthdate = LocalDate.now().minusYears(i.toLong())
            sex = if (i % 2 == 0) PetSex.MALE else PetSex.FEMALE
        }
    }

}
