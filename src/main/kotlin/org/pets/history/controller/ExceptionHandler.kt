package org.pets.history.controller

import org.pets.history.service.MediaTypeNotValidException
import org.pets.history.service.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.multipart.MaxUploadSizeExceededException


@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(exception: Exception): ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MediaTypeNotValidException::class)
    fun handleMediaTypeNotValid(exception: Exception): ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(exception: MethodArgumentNotValidException): ResponseEntity<Map<String, List<String>>> {
        val messages = exception.allErrors.mapNotNull { error -> error.defaultMessage }
        return ResponseEntity(getMessages(messages), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(exception: HttpMessageNotReadableException): ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun maxUploadSizeExceeded(exception: Exception): ResponseEntity<RuntimeException> {
        return ResponseEntity(
            RuntimeException("El tamaño máximo de archivo de 10 mb fue excedido"),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(exception: Exception): ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(BadCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleBadCredentials(exception: BadCredentialsException): ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUsernameNotFoundException(exception: UsernameNotFoundException): ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthenticationException(exception: AuthenticationException): ResponseEntity<RuntimeException> {
        return ResponseEntity(RuntimeException(exception.message), HttpStatus.UNAUTHORIZED)
    }

    private fun getMessages(messages: List<String>): Map<String, List<String>> {
        val response: MutableMap<String, List<String>> = HashMap()
        response["messages"] = messages
        return response
    }
}
