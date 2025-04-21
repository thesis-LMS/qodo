package com.library.system.model

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Custom exception hierarchy for the Library Management System.
 * Using @ResponseStatus maps these exceptions to HTTP status codes automatically
 * when thrown from a @Controller.
 */

@ResponseStatus(HttpStatus.NOT_FOUND) // 404
class ResourceNotFoundException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.CONFLICT) // 409
class BookNotAvailableException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.CONFLICT) // 409
class BookAlreadyReturnedException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.BAD_REQUEST) // 400
class InvalidInputException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.CONFLICT) // 409 - Or maybe 400 Bad Request?
class BorrowingLimitExceededException(message: String) : RuntimeException(message)
