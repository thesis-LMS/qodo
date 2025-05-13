package com.library.system.repository

import com.library.system.model.Book
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface BookRepository : JpaRepository<Book, UUID> { // Changed this line
    // Custom query methods can be defined here if needed, Spring Data JPA will implement them.
    // For example, if you still need the specific search methods and JpaRepository doesn't cover them directly
    // via its naming conventions, you might need @Query annotations or keep them if they follow conventions.
    // JpaRepository already provides:
    // save, findById, findAll, deleteById, existsById

    // These can often be replaced by JpaRepository's query derivation or more complex @Query annotations
    fun findByTitleContainingIgnoreCase(title: String): List<Book>

    fun findByAuthorContainingIgnoreCase(author: String): List<Book>

    fun findByAvailable(available: Boolean): List<Book>

    fun findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndAvailable(
        title: String,
        author: String,
        available: Boolean,
    ): List<Book>

    fun findByTitleContainingIgnoreCaseAndAvailable(
        title: String,
        available: Boolean,
    ): List<Book>

    fun findByAuthorContainingIgnoreCaseAndAvailable(
        author: String,
        available: Boolean,
    ): List<Book>

    fun findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(
        title: String,
        author: String,
    ): List<Book>
}
