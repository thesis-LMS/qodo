package com.library.system.web

import com.library.system.model.Book
import com.library.system.services.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun createBook(@RequestBody book: Book): ResponseEntity<Book> =
        ResponseEntity.status(HttpStatus.CREATED).body(bookService.addBook(book)) // Ensure 201

    @GetMapping("/{id}")
    fun getBook(@PathVariable id: UUID): ResponseEntity<Book> =
        ResponseEntity.ok(bookService.getBookById(id))

    @GetMapping // Added this endpoint
    fun getAllBooks(): ResponseEntity<List<Book>> =
        ResponseEntity.ok(bookService.getAllBooks())

    @GetMapping("/search")
    fun searchBooks(
        @RequestParam(required = false) title: String?, // Added required = false for more flexibility
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) available: Boolean?
    ): ResponseEntity<List<Book>> =
        ResponseEntity.ok(bookService.searchBooks(title, author, available))

    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: UUID, @RequestBody book: Book): ResponseEntity<Book> =
        ResponseEntity.ok(bookService.updateBook(id, book))

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: UUID): ResponseEntity<Any> {
        bookService.deleteBookById(id)
        return ResponseEntity.noContent().build<Any>()
    }

    @PostMapping("/{id}/borrow")
    fun borrowBook(
        @PathVariable id: UUID,
        @RequestParam userId: UUID // Ensure this param name matches the test
    ): ResponseEntity<Book> =
        ResponseEntity.ok(bookService.borrowBook(id, userId))

    @PostMapping("/{id}/return")
    fun returnBook(@PathVariable id: UUID): ResponseEntity<Book> =
        ResponseEntity.ok(bookService.returnBook(id))
}