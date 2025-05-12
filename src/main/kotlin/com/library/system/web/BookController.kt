package com.library.system.web

import com.library.system.model.Book
import com.library.system.services.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST controller for managing book resources (/api/books).
 * Handles incoming HTTP requests related to books, search, borrowing, and returns,
 * delegating processing to the BookService.
 *
 * @param bookService The service layer dependency for book operations.
 */
@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService
) {

    /**
     * Handles POST requests to add a new book.
     * Endpoint: POST /api/books
     * @param book The book data from the request body.
     * @return The newly created book object.
     * Returns HTTP 201 (Created) on success.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@Valid @RequestBody book: Book): Book {
        return bookService.addBook(book)
    }

    /**
     * Handles GET requests to retrieve a specific book by its ID.
     * Endpoint: GET /api/books/{id}
     * @param id The UUID of the book to retrieve.
     * @return The found book object.
     * Returns HTTP 200 (OK) on success, 404 (Not Found) if not found.
     */
    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: UUID): Book {
        return bookService.getBookById(id)
        // ResourceNotFoundException -> 404 handled by @ResponseStatus on exception
    }

    /**
     * Handles GET requests to retrieve all books.
     * Endpoint: GET /api/books
     * @return A list of all book objects.
     * Returns HTTP 200 (OK).
     */
    @GetMapping
    fun getAllBooks(): List<Book> {
        return bookService.getAllBooks()
    }

    /**
     * Handles PUT requests to update an existing book.
     * Endpoint: PUT /api/books/{id}
     * @param id The UUID of the book to update.
     * @param updatedDetails The updated book data from the request body.
     * @return The updated book object.
     * Returns HTTP 200 (OK) on success, 404 (Not Found) if not found.
     */
    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: UUID, @Valid @RequestBody updatedDetails: Book): Book {
        return bookService.updateBook(id, updatedDetails)
    }

    /**
     * Handles DELETE requests to delete a book by its ID.
     * Endpoint: DELETE /api/books/{id}
     * @param id The UUID of the book to delete.
     * Returns HTTP 204 (No Content) on success, 404 (Not Found) if not found.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBookById(@PathVariable id: UUID) {
        bookService.deleteBookById(id)
    }

    /**
     * Handles GET requests to search for books based on criteria.
     * Endpoint: GET /api/books/search?title=...&author=...&available=...
     * @param title Optional title query parameter.
     * @param author Optional author query parameter.
     * @param available Optional availability query parameter.
     * @return A list of books matching the search criteria.
     * Returns HTTP 200 (OK).
     */
    @GetMapping("/search")
    fun searchBooks(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) available: Boolean?
    ): List<Book> {
        return bookService.searchBooks(title, author, available)
    }

    /**
     * Handles POST requests to borrow a book.
     * Endpoint: POST /api/books/{bookId}/borrow?userId=...
     * @param bookId The UUID of the book to borrow (from path).
     * @param userId The UUID of the user borrowing the book (from query parameter).
     * @return The updated book object reflecting the borrow status.
     * Returns HTTP 200 (OK) on success.
     * Returns 404 (Not Found) if book/user not found.
     * Returns 409 (Conflict) if book not available or user limit reached.
     */
    @PostMapping("/{bookId}/borrow")
    fun borrowBook(@PathVariable bookId: UUID, @RequestParam userId: UUID): Book {
        return bookService.borrowBook(bookId, userId)
    }

    /**
     * Handles POST requests to return a book.
     * Endpoint: POST /api/books/{bookId}/return
     * @param bookId The UUID of the book being returned (from path).
     * @return The updated book object reflecting the return status.
     * Returns HTTP 200 (OK) on success.
     * Returns 404 (Not Found) if book not found.
     * Returns 409 (Conflict) if book already returned/available.
     */
    @PostMapping("/{bookId}/return")
    fun returnBook(@PathVariable bookId: UUID): Book {
        return bookService.returnBook(bookId)
    }
}
