package org.pets.history.service

class NotFoundException(override val message: String) : RuntimeException(message)

class MediaTypeNotValidException(invalidMediaType: String, validMediaTypes: Iterable<String>) :
    RuntimeException(
        "El tipo de archivo $invalidMediaType no se encuentra dentro de los tipos de archivo soportados: ${
            validMediaTypes.joinToString(
                ", "
            )
        }"
    )
