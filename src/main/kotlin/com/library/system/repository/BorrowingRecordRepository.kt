package com.library.system.repository

import com.library.system.model.BorrowingRecord
import java.util.UUID
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface BorrowingRecordRepository {
    fun save(borrowingRecord: BorrowingRecord): BorrowingRecord
    fun findByBookIdAndReturnDateIsNull(bookId: UUID): Optional<BorrowingRecord>
    fun countByUserIdAndReturnDateIsNull(userId: UUID): Long
}