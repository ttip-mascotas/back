package org.pets.history.serializer

data class CollectionDTO<T>(val results: MutableIterable<T>)
