package org.pets.history.controller

import org.pets.history.service.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun notFoundHandler(exception: Exception) : ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(exception: MethodArgumentNotValidException): ResponseEntity<Map<String, List<String>>> {
        val messages = exception.allErrors.mapNotNull { error -> error.defaultMessage }

        return ResponseEntity(getMessages(messages), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleException(exception: HttpMessageNotReadableException): ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun unexpectedHandler(exception: Exception) : ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun getMessages(messages: List<String>): Map<String, List<String>> {
        val response: MutableMap<String, List<String>> = HashMap()
        response["messages"] = messages
        return response
    }
}