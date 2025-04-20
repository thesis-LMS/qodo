package com.library.system.serviceTests

import com.library.system.model.*
import com.library.system.repository.UserRepository
import com.library.system.services.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var userService: UserService


    @Test
    fun `registerUser should save and return the new user`() {
        val userId = UUID.randomUUID()
        val newUser = User(name = "John Doe", email = "john.doe@example.com", role = UserRole.MEMBER)
        val savedUser = newUser.copy(id = userId)

        `when`(userRepository.save(any(User::class.java))).thenReturn(savedUser)

        val result = userService.registerUser(newUser)

        assertNotNull(result)
        assertEquals(savedUser.id, result.id)
        assertEquals(newUser.name, result.name)
        assertEquals(newUser.email, result.email)
        verify(userRepository).save(newUser)
    }

    @Test
    fun `getUserById should return user when found`() {
        val userId = UUID.randomUUID()
        val existingUser = User(id = userId, name = "Jane Doe", email = "jane.doe@example.com")
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(existingUser))

        val result = userService.getUserById(userId)

        assertNotNull(result)
        assertEquals(existingUser, result)
        verify(userRepository).findById(userId)
    }

    @Test
    fun `getUserById should throw ResourceNotFoundException when user not found`() {
        val userId = UUID.randomUUID()
        `when`(userRepository.findById(userId)).thenReturn(Optional.empty())

        val exception = assertThrows<ResourceNotFoundException> {
            userService.getUserById(userId)
        }
        assertEquals("User with ID $userId not found", exception.message)
        verify(userRepository).findById(userId)
    }

    @Test
    fun `updateUser should update and return user when found`() {
        val userId = UUID.randomUUID()
        val existingUser = User(id = userId, name = "Old Name", email = "old@example.com")
        val updatedDetails = User(id = userId, name = "New Name", email = "new@example.com")
        val savedUser = updatedDetails.copy() // Simulate the saved state

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.save(any(User::class.java))).thenReturn(savedUser)

        val result = userService.updateUser(userId, updatedDetails)

        assertNotNull(result)
        assertEquals(userId, result.id)
        assertEquals(updatedDetails.name, result.name)
        assertEquals(updatedDetails.email, result.email)
        verify(userRepository).findById(userId)
        verify(userRepository).save(argThat { user -> user.id == userId && user.name == updatedDetails.name })
    }

    @Test
    fun `updateUser should throw ResourceNotFoundException when user not found`() {
        val userId = UUID.randomUUID()
        val userDetailsToUpdate = User(id = userId, name = "Doesn't Matter", email = "test@test.com")
        `when`(userRepository.findById(userId)).thenReturn(Optional.empty())

        val exception = assertThrows<ResourceNotFoundException> {
            userService.updateUser(userId, userDetailsToUpdate)
        }
        assertEquals("User with ID $userId not found for update", exception.message)
        verify(userRepository).findById(userId)
        verify(userRepository, never()).save(any(User::class.java))
    }
}
