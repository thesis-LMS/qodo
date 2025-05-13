package com.library.system.controllerTests

import com.fasterxml.jackson.databind.ObjectMapper
import com.library.system.model.*
import com.library.system.services.BookService
import com.library.system.web.BookController
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
import java.time.LocalDate
import java.util.UUID

@ExtendWith(SpringExtension::class)
@WebMvcTest(BookController::class)
class BookControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var bookService: BookService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `POST addBook should return created book and status 201`() {
        val bookId = UUID.randomUUID()
        val newBookDto = Book(title = "New Book", author = "New Author")
        val savedBook = newBookDto.copy(id = bookId, available = true)
        whenever(bookService.addBook(any<Book>())).thenReturn(savedBook)

        mockMvc
            .perform(
                post("/api/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newBookDto)),
            ).andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(savedBook.id.toString()))
            .andExpect(jsonPath("$.title").value(savedBook.title))
            .andExpect(jsonPath("$.available").value(true))

        verify(bookService).addBook(argThat { book -> book.title == newBookDto.title })
    }

    @Test
    fun `GET getBookById should return book and status 200 when found`() {
        val bookId = UUID.randomUUID()
        val existingBook = Book(id = bookId, title = "Found Book", author = "Author", available = true)
        whenever(bookService.getBookById(bookId)).thenReturn(existingBook)

        mockMvc
            .perform(
                get("/api/books/{id}", bookId)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(existingBook.id.toString()))
            .andExpect(jsonPath("$.title").value(existingBook.title))

        verify(bookService).getBookById(bookId)
    }

    @Test
    fun `GET getBookById should return status 404 when book not found`() {
        val bookId = UUID.randomUUID()
        whenever(bookService.getBookById(bookId)).thenThrow(ResourceNotFoundException("Book not found"))

        mockMvc
            .perform(
                get("/api/books/{id}", bookId)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isNotFound)

        verify(bookService).getBookById(bookId)
    }

    @Test
    fun `GET getAllBooks should return list of books and status 200`() {
        val book1Id = UUID.randomUUID()
        val book2Id = UUID.randomUUID()
        val books =
            listOf(
                Book(id = book1Id, title = "Book 1", author = "Author 1", available = true),
                Book(id = book2Id, title = "Book 2", author = "Author 2", available = false),
            )
        whenever(bookService.getAllBooks()).thenReturn(books)

        mockMvc
            .perform(
                get("/api/books")
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").value(books.size))
            .andExpect(jsonPath("$[0].id").value(book1Id.toString()))
            .andExpect(jsonPath("$[1].id").value(book2Id.toString()))

        verify(bookService).getAllBooks()
    }

    @Test
    fun `PUT updateBook should return updated book and status 200 when found`() {
        val bookId = UUID.randomUUID()
        val updateDto = Book(id = bookId, title = "Updated Title", author = "Updated Author", available = false)
        val updatedBook = updateDto.copy()
        whenever(bookService.updateBook(eq(bookId), any<Book>())).thenReturn(updatedBook)

        mockMvc
            .perform(
                put("/api/books/{id}", bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(bookId.toString()))
            .andExpect(jsonPath("$.title").value(updateDto.title))
            .andExpect(jsonPath("$.available").value(false))

        verify(bookService).updateBook(eq(bookId), argThat { book -> book.title == updateDto.title })
    }

    @Test
    fun `PUT updateBook should return status 404 when book not found`() {
        val bookId = UUID.randomUUID()
        val updateDto = Book(id = bookId, title = "Update Fail", author = "Fail Author")
        whenever(bookService.updateBook(eq(bookId), any<Book>()))
            .thenThrow(ResourceNotFoundException("Book not found"))

        mockMvc
            .perform(
                put("/api/books/{id}", bookId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)),
            ).andExpect(status().isNotFound)

        verify(bookService).updateBook(eq(bookId), any<Book>())
    }

    @Test
    fun `DELETE deleteBookById should return status 204 when book exists`() {
        val bookId = UUID.randomUUID()
        doNothing().whenever(bookService).deleteBookById(bookId)

        mockMvc
            .perform(delete("/api/books/{id}", bookId))
            .andExpect(status().isNoContent)

        verify(bookService).deleteBookById(bookId)
    }

    @Test
    fun `DELETE deleteBookById should return status 404 when book does not exist`() {
        val bookId = UUID.randomUUID()
        doThrow(ResourceNotFoundException("Book not found")).whenever(bookService).deleteBookById(bookId)

        mockMvc
            .perform(delete("/api/books/{id}", bookId))
            .andExpect(status().isNotFound)

        verify(bookService).deleteBookById(bookId)
    }

    @Test
    fun `GET searchBooks by title should return matching books and status 200`() {
        val titleQuery = "Search"
        val bookId = UUID.randomUUID()
        val matchingBooks = listOf(Book(id = bookId, title = "Searchable Book", author = "Author", available = true))
        whenever(bookService.searchBooks(eq(titleQuery), isNull(), isNull())).thenReturn(matchingBooks)

        mockMvc
            .perform(
                get("/api/books/search")
                    .param("title", titleQuery)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.size()").value(matchingBooks.size))
            .andExpect(jsonPath("$[0].id").value(bookId.toString()))

        verify(bookService).searchBooks(eq(titleQuery), isNull(), isNull())
    }

    @Test
    fun `GET searchBooks by author should return matching books and status 200`() {
        val authorQuery = "Finder"
        val bookId = UUID.randomUUID()
        val matchingBooks = listOf(Book(id = bookId, title = "Book", author = "Finder Author", available = true))
        whenever(bookService.searchBooks(isNull(), eq(authorQuery), isNull())).thenReturn(matchingBooks)

        mockMvc
            .perform(
                get("/api/books/search")
                    .param("author", authorQuery)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(matchingBooks.size))
            .andExpect(jsonPath("$[0].id").value(bookId.toString()))

        verify(bookService).searchBooks(isNull(), eq(authorQuery), isNull())
    }

    @Test
    fun `GET searchBooks by availability should return matching books and status 200`() {
        val availabilityQuery = "true"
        val bookId = UUID.randomUUID()
        val matchingBooks = listOf(Book(id = bookId, title = "Book", author = "Author", available = true))
        whenever(bookService.searchBooks(isNull(), isNull(), eq(true))).thenReturn(matchingBooks)

        mockMvc
            .perform(
                get("/api/books/search")
                    .param("available", availabilityQuery)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(matchingBooks.size))
            .andExpect(jsonPath("$[0].id").value(bookId.toString()))

        verify(bookService).searchBooks(isNull(), isNull(), eq(true))
    }

    @Test
    fun `GET searchBooks with multiple criteria should return matching books and status 200`() {
        val titleQuery = "Combo"
        val availabilityQuery = "false"
        val bookId = UUID.randomUUID()
        val matchingBooks = listOf(Book(id = bookId, title = "Combo Book", author = "Author", available = false))
        whenever(bookService.searchBooks(eq(titleQuery), isNull(), eq(false))).thenReturn(matchingBooks)

        mockMvc
            .perform(
                get("/api/books/search")
                    .param("title", titleQuery)
                    .param("available", availabilityQuery)
                    .accept(MediaType.APPLICATION_JSON),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.size()").value(matchingBooks.size))
            .andExpect(jsonPath("$[0].id").value(bookId.toString()))

        verify(bookService).searchBooks(eq(titleQuery), isNull(), eq(false))
    }

    @Test
    fun `POST borrowBook should call service and return status 200 on success`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val borrowedBook =
            Book(
                id = bookId,
                title = "Borrowed",
                author = "Author",
                available = false,
                borrowedByUserId = userId,
                dueDate = LocalDate.now().plusWeeks(2),
            )
        // assuming service returns the updated Book for simplicity here
        whenever(bookService.borrowBook(bookId, userId)).thenReturn(borrowedBook)

        mockMvc
            .perform(
                post("/api/books/{bookId}/borrow", bookId)
                    .param("userId", userId.toString()),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(bookId.toString()))
            .andExpect(jsonPath("$.available").value(false))
            .andExpect(jsonPath("$.borrowedByUserId").value(userId.toString()))

        verify(bookService).borrowBook(bookId, userId)
    }

    @Test
    fun `POST borrowBook should return status 404 when book not found`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        whenever(bookService.borrowBook(bookId, userId)).thenThrow(ResourceNotFoundException("Book not found"))

        mockMvc
            .perform(
                post("/api/books/{bookId}/borrow", bookId)
                    .param("userId", userId.toString()),
            ).andExpect(status().isNotFound)

        verify(bookService).borrowBook(bookId, userId)
    }

    @Test
    fun `POST borrowBook should return status 404 when user not found`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        whenever(bookService.borrowBook(bookId, userId)).thenThrow(ResourceNotFoundException("User not found"))

        mockMvc
            .perform(
                post("/api/books/{bookId}/borrow", bookId)
                    .param("userId", userId.toString()),
            ).andExpect(status().isNotFound)

        verify(bookService).borrowBook(bookId, userId)
    }

    @Test
    fun `POST borrowBook should return status 409 when book not available`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        whenever(bookService.borrowBook(bookId, userId)).thenThrow(BookNotAvailableException("Book not available"))

        mockMvc
            .perform(
                post("/api/books/{bookId}/borrow", bookId)
                    .param("userId", userId.toString()),
            ).andExpect(status().isConflict)

        verify(bookService).borrowBook(bookId, userId)
    }

    @Test
    fun `POST returnBook should call service and return status 200 on success`() {
        val bookId = UUID.randomUUID()
        val returnedBook =
            Book(id = bookId, title = "Returned", author = "Author", available = true, borrowedByUserId = null, dueDate = null)
        // assuming service returns the updated Book for simplicity here
        whenever(bookService.returnBook(bookId)).thenReturn(returnedBook)

        mockMvc
            .perform(post("/api/books/{bookId}/return", bookId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(bookId.toString()))
            .andExpect(jsonPath("$.available").value(true))
            .andExpect(jsonPath("$.borrowedByUserId").isEmpty)

        verify(bookService).returnBook(bookId)
    }

    @Test
    fun `POST returnBook should return status 404 when book not found`() {
        val bookId = UUID.randomUUID()
        whenever(bookService.returnBook(bookId)).thenThrow(ResourceNotFoundException("Book not found"))

        mockMvc
            .perform(post("/api/books/{bookId}/return", bookId))
            .andExpect(status().isNotFound)

        verify(bookService).returnBook(bookId)
    }

    @Test
    fun `POST returnBook should return status 409 when book already available`() {
        val bookId = UUID.randomUUID()
        whenever(bookService.returnBook(bookId)).thenThrow(BookAlreadyReturnedException("Book already available"))

        mockMvc
            .perform(post("/api/books/{bookId}/return", bookId))
            .andExpect(status().isConflict)

        verify(bookService).returnBook(bookId)
    }
}
