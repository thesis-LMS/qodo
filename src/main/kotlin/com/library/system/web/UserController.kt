package com.library.system.web

import com.library.system.model.User
import com.library.system.services.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST controller for managing user resources (/api/users).
 * Handles incoming HTTP requests and delegates processing to the UserService.
 *
 * @param userService The service layer dependency for user operations.
 */
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    /**
     * Handles POST requests to register a new user.
     * Endpoint: POST /api/users
     * @param user The user data from the request body. Input validation is triggered by @Valid.
     * @return The newly created user object with its assigned ID.
     * Returns HTTP 201 (Created) on success.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@Valid @RequestBody user: User): User {
        return userService.registerUser(user)
    }

    /**
     * Handles GET requests to retrieve a specific user by their ID.
     * Endpoint: GET /api/users/{id}
     * @param id The UUID of the user to retrieve, extracted from the path.
     * @return The found user object.
     * Returns HTTP 200 (OK) on success.
     * Returns HTTP 404 (Not Found) if the user doesn't exist (handled by ResourceNotFoundException).
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): User {
        return userService.getUserById(id)
    }

    /**
     * Handles PUT requests to update an existing user.
     * Endpoint: PUT /api/users/{id}
     * @param id The UUID of the user to update, extracted from the path.
     * @param updatedDetails The updated user data from the request body. Input validation is triggered by @Valid.
     * @return The updated user object.
     * Returns HTTP 200 (OK) on success.
     * Returns HTTP 404 (Not Found) if the user doesn't exist (handled by ResourceNotFoundException).
     */
    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @Valid @RequestBody updatedDetails: User): User {
        return userService.updateUser(id, updatedDetails)
    }
}
