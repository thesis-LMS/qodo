package com.library.system.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDate
import java.util.UUID

/**
 * Represents a book in the library's collection.
 * Uses UUID as the primary key.
 * This class will be mapped to the 'books' table in the database.
 */
@Entity
@Table(name = "books")
data class Book(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @field:NotEmpty(message = "Book title cannot be empty")
    @Column(nullable = false)
    var title: String,

    @field:NotEmpty(message = "Book author cannot be empty")
    @Column(nullable = false)
    var author: String,

    @Column(nullable = false)
    var available: Boolean = true,

    var borrowedByUserId: UUID? = null,

    var dueDate: LocalDate? = null
) {
}
