package com.library.system.serviceTests

import com.library.system.model.User
import com.library.system.services.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should return user by ID`() {
        val user = User(1, "John Doe", "john.doe@example.com")
        Mockito.`when`(userService.getUserById(1)).thenReturn(user)

        val actualUser = userService.getUserById(1)

        Assertions.assertEquals(user, actualUser)
        Mockito.verify(userService).getUserById(1)
    }

    @Test
    fun `should create new user`() {
        val user = User(2, "Jane Doe", "jane.doe@example.com")
        Mockito.doNothing().`when`(userService).createUser(user)

        userService.createUser(user)

        Mockito.verify(userService).createUser(user)
    }
}