package com.library.system.model

import java.time.LocalDate
import java.util.UUID

data class BorrowingRecord(
    val id: UUID = UUID.randomUUID(),
    val bookId: UUID,
    val userId: UUID,
    val borrowDate: LocalDate,
    val dueDate: LocalDate?,
    var returnDate: LocalDate?,
    var lateFee: Double
)