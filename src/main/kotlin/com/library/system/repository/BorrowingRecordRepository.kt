package com.library.system.repository

import com.library.system.model.BorrowingRecord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

/**
 * Spring Data JPA repository for accessing and managing BorrowingRecord entities.
 */
@Repository
interface BorrowingRecordRepository : JpaRepository<BorrowingRecord, UUID> {

    /**
     * Counts the number of active borrowing records (where returnDate is null) for a specific user.
     * Used to check borrowing limits.
     * @param userId The UUID of the user.
     * @return The count of active borrows for the user.
     */
    fun countByUserIdAndReturnDateIsNull(userId: UUID): Long

    /**
     * Finds the active borrowing record (where returnDate is null) for a specific book.
     * Used when returning a book to find the corresponding record to update.
     * @param bookId The UUID of the book.
     * @return An Optional containing the active BorrowingRecord if found, otherwise empty.
     */
    fun findByBookIdAndReturnDateIsNull(bookId: UUID): Optional<BorrowingRecord>
}
