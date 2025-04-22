package com.library.system.services

import com.library.system.model.*
import com.library.system.repository.UserRepository
import java.util.UUID

class UserService(private val userRepository: UserRepository) {

    fun registerUser(user: User): User {
        return userRepository.save(user)
    }

    fun getUserById(id: UUID): User {
        return userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("User with ID $id not found")
        }
    }

    fun updateUser(id: UUID, updatedUser: User): User {
        val existingUser = userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("User with ID $id not found")
        }
        val userToSave = existingUser.copy(
            name = updatedUser.name,
            email = updatedUser.email,
            role = updatedUser.role
        )
        return userRepository.save(userToSave)
    }
}
