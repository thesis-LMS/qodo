package com.library.system.web

import com.library.system.model.User
import com.library.system.services.UserService
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun registerUser(@RequestBody user: User): User = userService.registerUser(user)

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): User = userService.getUserById(id)

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody user: User): User =
        userService.updateUser(id, user)
}