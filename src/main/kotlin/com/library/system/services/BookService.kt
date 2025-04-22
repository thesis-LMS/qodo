package com.library.system.services

import com.library.system.model.BookAlreadyReturnedException
import com.library.system.model.BookNotAvailableException
import com.library.system.model.BorrowingLimitExceededException
import com.library.system.model.ResourceNotFoundException
import com.library.system.model.Book
import com.library.system.model.BorrowingRecord
import com.library.system.repository.BookRepository
import com.library.system.repository.BorrowingRecordRepository
import com.library.system.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val borrowingRecordRepository: BorrowingRecordRepository
) {

    fun addBook(book: Book): Book {
        return bookRepository.save(book)
    }

    fun getBookById(id: UUID): Book {
        return bookRepository.findById(id).orElseThrow { ResourceNotFoundException("Book with ID $id not found") }
    }

    fun getAllBooks(): List<Book> {
        return bookRepository.findAll()
    }

    fun updateBook(id: UUID, updatedBook: Book): Book {
        val existingBook = bookRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Book with ID $id not found") }

        val updated = existingBook.copy(
            title = updatedBook.title,
            author = updatedBook.author,
            available = updatedBook.available
        )
        return bookRepository.save(updated)
    }

    fun deleteBookById(id: UUID) {
        if (!bookRepository.existsById(id)) {
            throw ResourceNotFoundException("Book with ID $id not found for deletion")
        }
        bookRepository.deleteById(id)
    }

    fun searchBooks(title: String?, author: String?, available: Boolean?): List<Book> {
        return when {
            !title.isNullOrEmpty() && !author.isNullOrEmpty() && available != null -> {
                bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndAvailable(title, author, available)
            }
            !title.isNullOrEmpty() -> {
                bookRepository.findByTitleContainingIgnoreCase(title)
            }
            !author.isNullOrEmpty() -> {
                bookRepository.findByAuthorContainingIgnoreCase(author)
            }
            available != null -> {
                bookRepository.findByAvailable(available)
            }
            else -> {
                bookRepository.findAll()
            }
        }
    }

    fun borrowBook(bookId: UUID, userId: UUID): Book {
        val book = bookRepository.findById(bookId)
            .orElseThrow { ResourceNotFoundException("Book with ID $bookId not found") }
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User with ID $userId not found for borrowing.") }

        if (!book.available) {
            throw BookNotAvailableException("Book with ID $bookId is not available for borrowing.")
        }

        val currentBorrowCount = borrowingRecordRepository.countByUserIdAndReturnDateIsNull(userId)
        if (currentBorrowCount >= 5) {
            throw BorrowingLimitExceededException("User with ID $userId has reached the borrowing limit of 5 books.")
        }

        book.available = false
        book.borrowedByUserId = userId
        book.dueDate = LocalDate.now().plusWeeks(2)
        bookRepository.save(book)

        val borrowingRecord = BorrowingRecord(
            id = UUID.randomUUID(),
            bookId = bookId,
            userId = userId,
            borrowDate = LocalDate.now(),
            dueDate = book.dueDate,
            returnDate = null,
            lateFee = 0.0
        )
        borrowingRecordRepository.save(borrowingRecord)

        return book
    }

    fun returnBook(bookId: UUID): Book {
        val book = bookRepository.findById(bookId)
            .orElseThrow { ResourceNotFoundException("Book with ID $bookId not found") }

        val borrowingRecord = borrowingRecordRepository.findByBookIdAndReturnDateIsNull(bookId)
            .orElseThrow { BookAlreadyReturnedException("Book with ID $bookId is already available or no active borrowing record found.") }

        book.available = true
        book.borrowedByUserId = null
        book.dueDate = null
        bookRepository.save(book)

        borrowingRecord.returnDate = LocalDate.now()
        if (borrowingRecord.dueDate?.isBefore(borrowingRecord.returnDate) == true) {
            val daysOverdue = LocalDate.now().toEpochDay() - borrowingRecord.dueDate!!.toEpochDay()
            borrowingRecord.lateFee = daysOverdue * 0.5
        } else {
            borrowingRecord.lateFee = 0.0
        }
        borrowingRecordRepository.save(borrowingRecord)

        return book
    }
}