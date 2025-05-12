package com.library.system.web // Or your chosen package for advice

import com.library.system.model.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BookControllerAdvice {
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(e: ResourceNotFoundException): ResponseEntity<Map<String, String?>> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("message" to e.message))

    @ExceptionHandler(BookNotAvailableException::class)
    fun handleBookNotAvailable(e: BookNotAvailableException): ResponseEntity<Map<String, String?>> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(mapOf("message" to e.message))

    @ExceptionHandler(BookAlreadyReturnedException::class)
    fun handleBookAlreadyReturned(e: BookAlreadyReturnedException): ResponseEntity<Map<String, String?>> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(mapOf("message" to e.message))

    @ExceptionHandler(BorrowingLimitExceededException::class)
    fun handleBorrowingLimitExceeded(e: BorrowingLimitExceededException): ResponseEntity<Map<String, String?>> =
        ResponseEntity
            .status(HttpStatus.CONFLICT) // Or 400 BAD_REQUEST or 403 FORBIDDEN, depending on desired semantics
            .body(mapOf("message" to e.message))
}