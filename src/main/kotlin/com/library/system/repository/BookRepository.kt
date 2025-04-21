package com.library.system.repository

import com.library.system.model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Spring Data JPA repository for accessing and managing Book entities.
 * Provides standard CRUD operations and custom finder methods.
 */
@Repository
interface BookRepository : JpaRepository<Book, UUID> {

    fun findByTitleContainingIgnoreCase(title: String): List<Book>

    fun findByAuthorContainingIgnoreCase(author: String): List<Book>

    fun findByAvailable(available: Boolean): List<Book>

    fun findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndAvailable(
        title: String,
        author: String,
        available: Boolean
    ): List<Book>

    fun findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(
        title: String,
        author: String
    ): List<Book>

    fun findByTitleContainingIgnoreCaseAndAvailable(
        title: String,
        available: Boolean
    ): List<Book>

    fun findByAuthorContainingIgnoreCaseAndAvailable(
        author: String,
        available: Boolean
    ): List<Book>
}
