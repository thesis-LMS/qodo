package com.library.system.repository

import com.library.system.model.BorrowingRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.Optional

@Repository
interface BorrowingRecordRepository : JpaRepository<BorrowingRecord, UUID> { // Changed this line

    fun findByBookIdAndReturnDateIsNull(bookId: UUID): Optional<BorrowingRecord>
    fun countByUserIdAndReturnDateIsNull(userId: UUID): Long
    // JpaRepository provides save, findById, findAll, etc.
}