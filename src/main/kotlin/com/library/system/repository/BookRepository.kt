package com.library.system.repository

import com.library.system.model.Book
import java.util.UUID
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface BookRepository {
    fun save(book: Book): Book
    fun findById(id: UUID): Optional<Book>
    fun findAll(): List<Book>
    fun deleteById(id: UUID)
    fun existsById(id: UUID): Boolean
    fun findByTitleContainingIgnoreCase(title: String): List<Book>
    fun findByAuthorContainingIgnoreCase(author: String): List<Book>
    fun findByAvailable(available: Boolean): List<Book>
    fun findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndAvailable(title: String, author: String, available: Boolean): List<Book>
}