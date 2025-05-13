package com.library.system.controllerTests

import com.fasterxml.jackson.databind.ObjectMapper
import com.library.system.model.*
import com.library.system.services.UserService
import com.library.system.web.UserController
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.UUID

@ExtendWith(SpringExtension::class)
@WebMvcTest(UserController::class)
class UserControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `POST registerUser should return created user and status 201`() {
        val userId = UUID.randomUUID()
        val newUserDto = User(name = "New User", email = "new@example.com", role = UserRole.MEMBER)
        val savedUser = newUserDto.copy(id = userId)
        whenever(userService.registerUser(any<User>())).thenReturn(savedUser)

        mockMvc
            .perform(
                post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUserDto)),
            ).andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(savedUser.id.toString()))
            .andExpect(jsonPath("$.name").value(savedUser.name))
            .andExpect(jsonPath("$.email").value(savedUser.email))

        verify(userService).registerUser(argThat { user -> user.name == newUserDto.name })
    }

    @Test
    fun `GET getUserById should return user and status 200 when found`() {
        val userId = UUID.randomUUID()
        val existingUser = User(id = userId, name = "Found User", email = "found@example.com")
        whenever(userService.getUserById(userId)).thenReturn(existingUser)

        mockMvc
            .perform(
                get("/api/users/{id}", userId)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(existingUser.id.toString()))
            .andExpect(jsonPath("$.name").value(existingUser.name))

        verify(userService).getUserById(userId)
    }

    @Test
    fun `GET getUserById should return status 404 when user not found`() {
        val userId = UUID.randomUUID()
        whenever(userService.getUserById(userId)).thenThrow(ResourceNotFoundException("User with ID $userId not found"))

        mockMvc
            .perform(
                get("/api/users/{id}", userId)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isNotFound)

        verify(userService).getUserById(userId)
    }

    @Test
    fun `PUT updateUser should return updated user and status 200 when found`() {
        val userId = UUID.randomUUID()
        val updateDto = User(id = userId, name = "Updated Name", email = "updated@example.com")
        val updatedUser = updateDto.copy()
        whenever(userService.updateUser(eq(userId), any<User>())).thenReturn(updatedUser)

        mockMvc
            .perform(
                put("/api/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(userId.toString()))
            .andExpect(jsonPath("$.name").value(updateDto.name))

        verify(userService).updateUser(eq(userId), argThat { user -> user.name == updateDto.name })
    }

    @Test
    fun `PUT updateUser should return status 404 when user not found`() {
        val userId = UUID.randomUUID()
        val updateDto = User(id = userId, name = "Update Attempt", email = "update@fail.com")
        whenever(userService.updateUser(eq(userId), any<User>()))
            .thenThrow(ResourceNotFoundException("User with ID $userId not found for update"))

        mockMvc
            .perform(
                put("/api/users/{id}", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)),
            ).andExpect(status().isNotFound)

        verify(userService).updateUser(eq(userId), any<User>())
    }
}
