package com.library.system.web // Or your chosen package for advice

import com.library.system.model.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class UserControllerAdvice {
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(e: ResourceNotFoundException): ResponseEntity<Map<String, String?>> {
        // The tests expect a 404, and this advice will provide it.
        // The body can be asserted if the test is written to do so.
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(mapOf("message" to e.message)) // Ensure a body is sent
    }
}