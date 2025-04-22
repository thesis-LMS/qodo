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
    fun addBook(@RequestBody book: Book): ResponseEntity<Book> {
        val newBook = bookService.addBook(book)
        return ResponseEntity(newBook, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: UUID): ResponseEntity<Book> {
        return try {
            val book = bookService.getBookById(id)
            ResponseEntity.ok(book)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAllBooks(): ResponseEntity<List<Book>> {
        val books = bookService.getAllBooks()
        return ResponseEntity.ok(books)
    }

    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: UUID, @RequestBody updatedBook: Book): ResponseEntity<Book> {
        return try {
            val book = bookService.updateBook(id, updatedBook)
            ResponseEntity.ok(book)
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: UUID): ResponseEntity<Void> {
        return try {
            bookService.deleteBookById(id)
            ResponseEntity.noContent().build()
        } catch (e: Exception) {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    fun searchBooks(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) available: Boolean?
    ): ResponseEntity<List<Book>> {
        val books = bookService.searchBooks(title, author, available)
        return ResponseEntity.ok(books)
    }

    @PostMapping("/{bookId}/borrow")
    fun borrowBook(
        @PathVariable bookId: UUID,
        @RequestParam userId: UUID
    ): ResponseEntity<Book> {
        val book = bookService.borrowBook(bookId, userId)
        return ResponseEntity.ok(book)
    }

    @PostMapping("/{bookId}/return")
    fun returnBook(@PathVariable bookId: UUID): ResponseEntity<Book> {
        val book = bookService.returnBook(bookId)
        return ResponseEntity.ok(book)
    }
}