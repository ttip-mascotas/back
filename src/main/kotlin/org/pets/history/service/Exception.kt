package org.pets.history.service

class NotFoundException(override val message: String) : RuntimeException(message)
