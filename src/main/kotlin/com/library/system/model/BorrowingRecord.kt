package com.library.system.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDate
import java.util.UUID

/**
 * Represents a record of a book being borrowed by a user.
 * Uses UUID as the primary key and for foreign key references.
 * This class will be mapped to the 'borrowing_records' table.
 */
@Entity
@Table(name = "borrowing_records")
data class BorrowingRecord(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @field:NotNull
    @Column(name = "book_id", nullable = false)
    val bookId: UUID,

    @field:NotNull
    @Column(name = "user_id", nullable = false)
    val userId: UUID,

    @Column(nullable = false)
    val borrowDate: LocalDate,

    @Column(nullable = false)
    val dueDate: LocalDate,

    var returnDate: LocalDate? = null,

    @field:PositiveOrZero(message = "Late fee cannot be negative")
    @Column(nullable = false)
    var lateFee: Double = 0.0
) {
}
