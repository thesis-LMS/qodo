package com.library.system.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate
import java.util.UUID

@Entity
data class BorrowingRecord(
    @Id
    val id: UUID = UUID.randomUUID(),
    val bookId: UUID,
    val userId: UUID,
    val borrowDate: LocalDate,
    @Column(nullable = true)
    val dueDate: LocalDate?,
    @Column(nullable = true)
    var returnDate: LocalDate?,
    var lateFee: Double,
)
