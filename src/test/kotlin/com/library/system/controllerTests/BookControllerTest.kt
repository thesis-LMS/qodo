package com.library.system.controllerTests

import com.library.system.web.BookController

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest

@WebMvcTest(BookController::class)
@ExtendWith(MockitoExtension::class)
class BookControllerTest {

    @Test
    fun `should return all books`() {
    }

    @Test
    fun `should add a new book`() {
    }
}
