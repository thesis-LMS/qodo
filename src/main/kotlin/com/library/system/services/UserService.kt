package com.library.system.services

import com.library.system.model.ResourceNotFoundException
import com.library.system.model.User
import com.library.system.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * Service layer for managing users (members, librarians, admins).
 * Implements the business logic defined by the requirements and tested in UserServiceTest.
 *
 * @param userRepository Repository for accessing user data.
 */
@Service
class UserService(
    private val userRepository: UserRepository
) {

    /**
     * Registers a new user in the system.
     * In a real application, you might add checks for existing emails.
     * @param user The user object to register (without ID).
     * @return The saved user object with its generated ID.
     */
    @Transactional
    fun registerUser(user: User): User {
        return userRepository.save(user)
    }

    /**
     * Retrieves a user by their unique ID.
     * @param id The UUID of the user to retrieve.
     * @return The found User object.
     * @throws ResourceNotFoundException if no user with the given ID exists.
     */
    fun getUserById(id: UUID): User {
        return userRepository.findById(id).orElseThrow {
            ResourceNotFoundException("User with ID $id not found")
        }
    }

    /**
     * Updates an existing user's details.
     * @param id The UUID of the user to update.
     * @param updatedDetails A User object containing the new details (ID should match).
     * @return The updated and saved User object.
     * @throws ResourceNotFoundException if no user with the given ID exists.
     */
    @Transactional
    fun updateUser(id: UUID, updatedDetails: User): User {
        val existingUser = getUserById(id)

        existingUser.name = updatedDetails.name
        existingUser.email = updatedDetails.email
        existingUser.role = updatedDetails.role

        return userRepository.save(existingUser)
    }
}
