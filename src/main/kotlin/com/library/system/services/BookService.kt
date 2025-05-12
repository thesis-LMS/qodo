package com.library.system.services

import com.library.system.model.*
import com.library.system.repository.BookRepository
import com.library.system.repository.BorrowingRecordRepository
import com.library.system.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Service layer for managing books and borrowing operations.
 * Implements the business logic defined by the requirements and tested in BookServiceTest.
 *
 * @param bookRepository Repository for accessing book data.
 * @param userRepository Repository for accessing user data.
 * @param borrowingRecordRepository Repository for accessing borrowing record data.
 */
@Service
class BookService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val borrowingRecordRepository: BorrowingRecordRepository
) {

    companion object {
        const val BORROWING_LIMIT: Long = 5
        const val LATE_FEE_PER_DAY: Double = 0.50
        const val BORROWING_WEEKS: Long = 2
    }

    /**
     * Adds a new book to the library collection.
     * @param book The book object to add (without ID).
     * @return The saved book object with its generated ID.
     */
    @Transactional
    fun addBook(book: Book): Book {
        return bookRepository.save(book.copy(available = true))
    }

    /**
     * Retrieves a book by its unique ID.
     * @param id The UUID of the book to retrieve.
     * @return The found Book object.
     * @throws ResourceNotFoundException if no book with the given ID exists.
     */
    fun getBookById(id: UUID): Book {
        return bookRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Book with ID $id not found")
        }
    }

    /**
     * Retrieves all books currently in the repository.
     * @return A list of all Book objects.
     */
    fun getAllBooks(): List<Book> {
        return bookRepository.findAll()
    }

    /**
     * Updates an existing book's details.
     * @param id The UUID of the book to update.
     * @param updatedDetails A Book object containing the new details (ID should match).
     * @return The updated and saved Book object.
     * @throws ResourceNotFoundException if no book with the given ID exists.
     */
    @Transactional
    fun updateBook(id: UUID, updatedDetails: Book): Book {
        val existingBook = getBookById(id)

        existingBook.title = updatedDetails.title
        existingBook.author = updatedDetails.author

        return bookRepository.save(existingBook)
    }

    /**
     * Deletes a book from the repository by its ID.
     * @param id The UUID of the book to delete.
     * @throws ResourceNotFoundException if no book with the given ID exists.
     */
    @Transactional
    fun deleteBookById(id: UUID) {
        if (!bookRepository.existsById(id)) {
            throw ResourceNotFoundException("Book with ID $id not found for deletion")
        }
        bookRepository.deleteById(id)
    }

    /**
     * Searches for books based on provided criteria.
     * Calls specific repository methods based on which criteria are present,
     * matching the methods mocked in the tests.
     * NOTE: A real implementation might use Specifications or Querydsl for more flexibility.
     *
     * @param title Optional title fragment to search for (case-insensitive).
     * @param author Optional author fragment to search for (case-insensitive).
     * @param available Optional availability status to filter by.
     * @return A list of books matching the criteria.
     */
    fun searchBooks(title: String?, author: String?, available: Boolean?): List<Book> {
        return when {
            title != null && author != null && available != null ->
                bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndAvailable(title, author, available)
            title != null && author != null ->
                bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(title, author)
            title != null && available != null ->
                bookRepository.findByTitleContainingIgnoreCaseAndAvailable(title, available)
            author != null && available != null ->
                bookRepository.findByAuthorContainingIgnoreCaseAndAvailable(author, available)
            title != null ->
                bookRepository.findByTitleContainingIgnoreCase(title)
            author != null ->
                bookRepository.findByAuthorContainingIgnoreCase(author)
            available != null ->
                bookRepository.findByAvailable(available)
            else ->
                bookRepository.findAll()
        }
    }

    /**
     * Allows a user to borrow an available book.
     * Updates the book's status and creates a borrowing record.
     *
     * @param bookId The UUID of the book to borrow.
     * @param userId The UUID of the user borrowing the book.
     * @return The updated Book object reflecting the borrow status.
     * @throws ResourceNotFoundException if the book or user is not found.
     * @throws BookNotAvailableException if the book is already borrowed.
     * @throws BorrowingLimitExceededException if the user has reached their borrowing limit.
     */
    @Transactional
    fun borrowBook(bookId: UUID, userId: UUID): Book {
        val book = getBookById(bookId)
        if (!book.available) {
            throw BookNotAvailableException("Book with ID $bookId is not available for borrowing.")
        }

        val user = userRepository.findById(userId).orElseThrow {
            ResourceNotFoundException("User with ID $userId not found for borrowing.")
        }

        val currentBorrows = borrowingRecordRepository.countByUserIdAndReturnDateIsNull(userId)
        if (currentBorrows >= BORROWING_LIMIT) {
            throw BorrowingLimitExceededException("User with ID $userId has reached the borrowing limit of $BORROWING_LIMIT books.")
        }

        val dueDate = LocalDate.now().plusWeeks(BORROWING_WEEKS)
        book.available = false
        book.borrowedByUserId = userId
        book.dueDate = dueDate
        val updatedBook = bookRepository.save(book)

        val borrowingRecord = BorrowingRecord(
            bookId = bookId,
            userId = userId,
            borrowDate = LocalDate.now(),
            dueDate = dueDate,
            returnDate = null,
            lateFee = 0.0
        )
        borrowingRecordRepository.save(borrowingRecord)

        return updatedBook
    }

    /**
     * Allows a user to return a borrowed book.
     * Updates the book's status and the corresponding borrowing record (calculating late fees).
     *
     * @param bookId The UUID of the book being returned.
     * @return The updated Book object reflecting the return status.
     * @throws ResourceNotFoundException if the book is not found.
     * @throws BookAlreadyReturnedException if the book is already available or has no active borrowing record.
     */
    @Transactional
    fun returnBook(bookId: UUID): Book {
        val book = getBookById(bookId)

        val borrowingRecord = borrowingRecordRepository.findByBookIdAndReturnDateIsNull(bookId)
            .orElseThrow {
                BookAlreadyReturnedException("Book with ID $bookId is already available or no active borrowing record found.")
            }

        if (book.available || book.borrowedByUserId == null) {
            throw BookAlreadyReturnedException("Book with ID $bookId is marked available but has an active borrowing record.")
        }


        val returnDate = LocalDate.now()
        var lateFee = 0.0
        if (returnDate.isAfter(borrowingRecord.dueDate)) {
            val daysOverdue = ChronoUnit.DAYS.between(borrowingRecord.dueDate, returnDate)
            lateFee = daysOverdue * LATE_FEE_PER_DAY
        }

        borrowingRecord.returnDate = returnDate
        borrowingRecord.lateFee = lateFee
        borrowingRecordRepository.save(borrowingRecord)

        book.available = true
        book.borrowedByUserId = null
        book.dueDate = null
        val updatedBook = bookRepository.save(book)

        return updatedBook
    }
}
