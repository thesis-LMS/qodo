package com.library.system.web

import com.library.system.model.User
import com.library.system.services.UserService
import org.springframework.http.HttpStatus // Added
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Added for clarity, or use ResponseEntity as below
    fun registerUser(
        @RequestBody user: User,
    ): User = userService.registerUser(user)
    // Alternative for more control, matching the BookController style:
    // fun registerUser(@RequestBody user: User): ResponseEntity<User> =
    //     ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(user))

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable id: UUID,
    ): User = userService.getUserById(id)

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: UUID,
        @RequestBody user: User,
    ): User = userService.updateUser(id, user)
}
