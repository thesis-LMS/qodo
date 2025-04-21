package com.library.system.serviceTests

import com.library.system.model.*
import com.library.system.repository.BookRepository
import com.library.system.repository.BorrowingRecordRepository
import com.library.system.repository.UserRepository
import com.library.system.services.BookService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.*

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    @Mock
    lateinit var bookRepository: BookRepository

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var borrowingRecordRepository: BorrowingRecordRepository

    @InjectMocks
    lateinit var bookService: BookService

    @Captor
    lateinit var borrowingRecordCaptor: ArgumentCaptor<BorrowingRecord>

    @Test
    fun `addBook should save and return the new book`() {
        val bookId = UUID.randomUUID()
        val newBook = Book(title = "The Great Novel", author = "Jane Author", available = true)
        val savedBook = newBook.copy(id = bookId)
        whenever(bookRepository.save(any<Book>())).thenReturn(savedBook)

        val result = bookService.addBook(newBook)

        assertNotNull(result)
        assertEquals(savedBook.id, result.id)
        assertEquals(newBook.title, result.title)
        assertTrue(result.available)
        verify(bookRepository).save(newBook)
    }

    @Test
    fun `getBookById should return book when found`() {
        val bookId = UUID.randomUUID()
        val existingBook = Book(id = bookId, title = "Existing Book", author = "Author", available = true)
        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook))

        val result = bookService.getBookById(bookId)

        assertEquals(existingBook, result)
        verify(bookRepository).findById(bookId)
    }

    @Test
    fun `getBookById should throw ResourceNotFoundException when book not found`() {
        val bookId = UUID.randomUUID()
        whenever(bookRepository.findById(bookId)).thenReturn(Optional.empty())

        val exception = assertThrows<ResourceNotFoundException> {
            bookService.getBookById(bookId)
        }
        assertEquals("Book with ID $bookId not found", exception.message)
        verify(bookRepository).findById(bookId)
    }

    @Test
    fun `getAllBooks should return list of books`() {
        val books = listOf(
            Book(id = UUID.randomUUID(), title = "Book 1", author = "Author 1", available = true),
            Book(id = UUID.randomUUID(), title = "Book 2", author = "Author 2", available = false)
        )
        whenever(bookRepository.findAll()).thenReturn(books)

        val result = bookService.getAllBooks()

        assertEquals(books, result)
        verify(bookRepository).findAll()
    }

    @Test
    fun `updateBook should update and return book when found`() {
        val bookId = UUID.randomUUID()
        val existingBook = Book(id = bookId, title = "Old Title", author = "Old Author", available = true)
        val updatedDetails = Book(id = bookId, title = "New Title", author = "New Author", available = false)
        val savedBook = updatedDetails.copy()

        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook))
        whenever(bookRepository.save(any<Book>())).thenReturn(savedBook)

        val result = bookService.updateBook(bookId, updatedDetails)

        assertNotNull(result)
        assertEquals(bookId, result.id)
        assertEquals(updatedDetails.title, result.title)
        assertEquals(updatedDetails.author, result.author)
        assertEquals(updatedDetails.available, result.available)
        verify(bookRepository).findById(bookId)
        verify(bookRepository).save(argThat { book -> book.id == bookId && book.title == updatedDetails.title })
    }

    @Test
    fun `updateBook should throw ResourceNotFoundException when book not found`() {
        val bookId = UUID.randomUUID()
        val bookDetailsToUpdate = Book(id = bookId, title = "Doesn't Matter", author = "Test")
        whenever(bookRepository.findById(bookId)).thenReturn(Optional.empty())

        val exception = assertThrows<ResourceNotFoundException> {
            bookService.updateBook(bookId, bookDetailsToUpdate)
        }
        assertEquals("Book with ID $bookId not found for update", exception.message)
        verify(bookRepository).findById(bookId)
        verify(bookRepository, never()).save(any<Book>())
    }

    @Test
    fun `deleteBookById should call repository delete when book exists`() {
        val bookId = UUID.randomUUID()
        whenever(bookRepository.existsById(bookId)).thenReturn(true)
        doNothing().whenever(bookRepository).deleteById(bookId)

        bookService.deleteBookById(bookId)

        verify(bookRepository).existsById(bookId)
        verify(bookRepository).deleteById(bookId)
    }

    @Test
    fun `deleteBookById should throw ResourceNotFoundException when book does not exist`() {
        val bookId = UUID.randomUUID()
        whenever(bookRepository.existsById(bookId)).thenReturn(false)

        val exception = assertThrows<ResourceNotFoundException> {
            bookService.deleteBookById(bookId)
        }
        assertEquals("Book with ID $bookId not found for deletion", exception.message)
        verify(bookRepository).existsById(bookId)
        verify(bookRepository, never()).deleteById(bookId)
    }

    @Test
    fun `searchBooksByTitle should return matching books`() {
        val titleQuery = "Novel"
        val matchingBooks = listOf(Book(id = UUID.randomUUID(), title = "The Great Novel", author = "Author", available = true))
        whenever(bookRepository.findByTitleContainingIgnoreCase(titleQuery)).thenReturn(matchingBooks)

        val result = bookService.searchBooks(title = titleQuery, author = null, available = null)

        assertEquals(matchingBooks, result)
        verify(bookRepository).findByTitleContainingIgnoreCase(titleQuery)
    }

    @Test
    fun `searchBooksByAuthor should return matching books`() {
        val authorQuery = "Jane"
        val matchingBooks = listOf(Book(id = UUID.randomUUID(), title = "Some Book", author = "Jane Author", available = true))
        whenever(bookRepository.findByAuthorContainingIgnoreCase(authorQuery)).thenReturn(matchingBooks)

        val result = bookService.searchBooks(title = null, author = authorQuery, available = null)

        assertEquals(matchingBooks, result)
        verify(bookRepository).findByAuthorContainingIgnoreCase(authorQuery)
    }

    @Test
    fun `searchBooksByAvailability should return matching books`() {
        val availabilityQuery = true
        val matchingBooks = listOf(Book(id = UUID.randomUUID(), title = "Available Book", author = "Author", available = true))
        whenever(bookRepository.findByAvailable(availabilityQuery)).thenReturn(matchingBooks)

        val result = bookService.searchBooks(title = null, author = null, available = availabilityQuery)

        assertEquals(matchingBooks, result)
        verify(bookRepository).findByAvailable(availabilityQuery)
    }

    @Test
    fun `searchBooks combined criteria should call appropriate repository method`() {
        val titleQuery = "Great"
        val authorQuery = "Jane"
        val availabilityQuery = true
        val matchingBooks = listOf(Book(id = UUID.randomUUID(), title = "Great Book", author = "Jane Doe", available = true))
        whenever(bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndAvailable(titleQuery, authorQuery, availabilityQuery))
            .thenReturn(matchingBooks)

        val result = bookService.searchBooks(title = titleQuery, author = authorQuery, available = availabilityQuery)

        assertEquals(matchingBooks, result)
        verify(bookRepository).findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndAvailable(titleQuery, authorQuery, availabilityQuery)
    }

    @Test
    fun `borrowBook should create borrowing record and update book when book available and user exists`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val bookToBorrow = Book(id = bookId, title = "Borrow Me", author = "Author", available = true)
        val userBorrowing = User(id = userId, name = "Borrower", email = "borrow@test.com")
        val expectedDueDate = LocalDate.now().plusWeeks(2)
        val borrowDate = LocalDate.now()

        whenever(borrowingRecordRepository.countByUserIdAndReturnDateIsNull(userId)).thenReturn(0L)
        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(bookToBorrow))
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(userBorrowing))
        whenever(bookRepository.save(any<Book>())).thenAnswer { invocation ->
            val savedBook = invocation.getArgument<Book>(0)
            assertFalse(savedBook.available)
            savedBook.copy()
        }
        whenever(borrowingRecordRepository.save(any<BorrowingRecord>())).thenAnswer { invocation ->
            val savedRecord = invocation.getArgument<BorrowingRecord>(0)
            savedRecord.copy(id = UUID.randomUUID())
        }

        bookService.borrowBook(bookId, userId)

        verify(bookRepository).save(argThat { book ->
            !book.available && book.borrowedByUserId == userId && book.dueDate == expectedDueDate
        })
        verify(borrowingRecordRepository).save(borrowingRecordCaptor.capture())
        val savedRecord = borrowingRecordCaptor.value
        assertEquals(bookId, savedRecord.bookId)
        assertEquals(userId, savedRecord.userId)
        assertEquals(borrowDate, savedRecord.borrowDate)
        assertEquals(expectedDueDate, savedRecord.dueDate)
        assertNull(savedRecord.returnDate)
        assertEquals(0.0, savedRecord.lateFee)
        verify(bookRepository).findById(bookId)
        verify(userRepository).findById(userId)
        verify(borrowingRecordRepository).countByUserIdAndReturnDateIsNull(userId)
    }

    @Test
    fun `borrowBook should throw BookNotAvailableException when book is not available`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val unavailableBook = Book(id = bookId, title = "Borrowed", author = "Author", available = false, borrowedByUserId = UUID.randomUUID())
        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(unavailableBook))

        val exception = assertThrows<BookNotAvailableException> {
            bookService.borrowBook(bookId, userId)
        }
        assertEquals("Book with ID $bookId is not available for borrowing.", exception.message)
        verify(bookRepository).findById(bookId)
        verify(userRepository, never()).findById(any<UUID>())
        verify(bookRepository, never()).save(any<Book>())
        verify(borrowingRecordRepository, never()).save(any<BorrowingRecord>())
        verify(borrowingRecordRepository, never()).countByUserIdAndReturnDateIsNull(any<UUID>())
    }

    @Test
    fun `borrowBook should throw ResourceNotFoundException when book not found`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        whenever(bookRepository.findById(bookId)).thenReturn(Optional.empty())

        val exception = assertThrows<ResourceNotFoundException> {
            bookService.borrowBook(bookId, userId)
        }
        assertEquals("Book with ID $bookId not found for borrowing.", exception.message)
        verify(bookRepository).findById(bookId)
        verify(userRepository, never()).findById(any<UUID>())
        verify(bookRepository, never()).save(any<Book>())
        verify(borrowingRecordRepository, never()).save(any<BorrowingRecord>())
        verify(borrowingRecordRepository, never()).countByUserIdAndReturnDateIsNull(any<UUID>())
    }

    @Test
    fun `borrowBook should throw ResourceNotFoundException when user not found`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val availableBook = Book(id = bookId, title = "Available", author = "Author", available = true)
        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(availableBook))
        whenever(userRepository.findById(userId)).thenReturn(Optional.empty())

        val exception = assertThrows<ResourceNotFoundException> {
            bookService.borrowBook(bookId, userId)
        }
        assertEquals("User with ID $userId not found for borrowing.", exception.message)
        verify(bookRepository).findById(bookId)
        verify(userRepository).findById(userId)
        verify(bookRepository, never()).save(any<Book>())
        verify(borrowingRecordRepository, never()).save(any<BorrowingRecord>())
    }

    @Test
    fun `returnBook should update book availability and borrowing record when book was borrowed`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val borrowedBook = Book(
            id = bookId, title = "Borrowed Book", author = "Author",
            available = false, borrowedByUserId = userId, dueDate = LocalDate.now().minusDays(5)
        )
        val borrowingRecord = BorrowingRecord(
            id = UUID.randomUUID(), bookId = bookId, userId = userId,
            borrowDate = LocalDate.now().minusWeeks(3),
            dueDate = LocalDate.now().minusDays(5),
            returnDate = null, lateFee = 0.0
        )
        val returnDate = LocalDate.now()
        val expectedLateFee = 5 * 0.5

        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(borrowedBook))
        whenever(borrowingRecordRepository.findByBookIdAndReturnDateIsNull(bookId)).thenReturn(Optional.of(borrowingRecord))
        whenever(bookRepository.save(any<Book>())).thenAnswer { invocation ->
            val savedBook = invocation.getArgument<Book>(0)
            assertTrue(savedBook.available)
            assertNull(savedBook.borrowedByUserId)
            assertNull(savedBook.dueDate)
            savedBook.copy()
        }
        whenever(borrowingRecordRepository.save(any<BorrowingRecord>())).thenAnswer { invocation ->
            val savedRecord = invocation.getArgument<BorrowingRecord>(0)
            assertEquals(returnDate, savedRecord.returnDate)
            assertEquals(expectedLateFee, savedRecord.lateFee)
            savedRecord.copy()
        }

        bookService.returnBook(bookId)

        verify(bookRepository).save(argThat { book ->
            book.available && book.borrowedByUserId == null && book.dueDate == null
        })
        verify(borrowingRecordRepository).save(borrowingRecordCaptor.capture())
        val savedRecord = borrowingRecordCaptor.value
        assertEquals(bookId, savedRecord.bookId)
        assertEquals(userId, savedRecord.userId)
        assertEquals(returnDate, savedRecord.returnDate)
        assertEquals(expectedLateFee, savedRecord.lateFee)
        verify(bookRepository).findById(bookId)
        verify(borrowingRecordRepository).findByBookIdAndReturnDateIsNull(bookId)
    }

    @Test
    fun `returnBook should throw BookAlreadyReturnedException when no active borrowing record found`() {
        val bookId = UUID.randomUUID()
        val availableBook = Book(id = bookId, title = "Available Book", author = "Author", available = true)
        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(availableBook))
        whenever(borrowingRecordRepository.findByBookIdAndReturnDateIsNull(bookId)).thenReturn(Optional.empty())

        val exception = assertThrows<BookAlreadyReturnedException> {
            bookService.returnBook(bookId)
        }
        assertEquals("Book with ID $bookId is already available or no active borrowing record found.", exception.message)
        verify(bookRepository).findById(bookId)
        verify(borrowingRecordRepository).findByBookIdAndReturnDateIsNull(bookId)
        verify(bookRepository, never()).save(any<Book>())
        verify(borrowingRecordRepository, never()).save(any<BorrowingRecord>())
    }

    @Test
    fun `returnBook should throw ResourceNotFoundException when book not found`() {
        val bookId = UUID.randomUUID()
        whenever(bookRepository.findById(bookId)).thenReturn(Optional.empty())

        val exception = assertThrows<ResourceNotFoundException> {
            bookService.returnBook(bookId)
        }
        assertEquals("Book with ID $bookId not found for return.", exception.message)
        verify(bookRepository).findById(bookId)
        verify(borrowingRecordRepository, never()).findByBookIdAndReturnDateIsNull(any<UUID>())
        verify(bookRepository, never()).save(any<Book>())
        verify(borrowingRecordRepository, never()).save(any<BorrowingRecord>())
    }

    @Test
    fun `returnBook should calculate late fee accurately when book is overdue`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val daysOverdue = 7L
        val feePerDay = 0.5
        val expectedFee = daysOverdue * feePerDay
        val dueDate = LocalDate.now().minusDays(daysOverdue)
        val returnDate = LocalDate.now()

        val borrowedBook = Book(id = bookId, title="Overdue", author="Author", available=false, borrowedByUserId=userId, dueDate=dueDate)
        val borrowingRecord = BorrowingRecord(id=UUID.randomUUID(), bookId=bookId, userId=userId, borrowDate=dueDate.minusWeeks(2), dueDate=dueDate, returnDate=null, lateFee=0.0)

        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(borrowedBook))
        whenever(borrowingRecordRepository.findByBookIdAndReturnDateIsNull(bookId)).thenReturn(Optional.of(borrowingRecord))
        whenever(bookRepository.save(any<Book>())).thenReturn(borrowedBook)
        whenever(borrowingRecordRepository.save(any<BorrowingRecord>())).thenAnswer { it.getArgument(0) }

        bookService.returnBook(bookId)

        verify(borrowingRecordRepository).save(borrowingRecordCaptor.capture())
        val savedRecord = borrowingRecordCaptor.value
        assertEquals(expectedFee, savedRecord.lateFee, 0.001)
        assertEquals(returnDate, savedRecord.returnDate)
    }

    @Test
    fun `returnBook should have zero late fee when returned on or before due date`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val dueDate = LocalDate.now().plusDays(1)
        val returnDate = LocalDate.now()

        val borrowedBook = Book(id = bookId, title="OnTime", author="Author", available=false, borrowedByUserId=userId, dueDate=dueDate)
        val borrowingRecord = BorrowingRecord(id=UUID.randomUUID(), bookId=bookId, userId=userId, borrowDate=dueDate.minusWeeks(1), dueDate=dueDate, returnDate=null, lateFee=0.0)

        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(borrowedBook))
        whenever(borrowingRecordRepository.findByBookIdAndReturnDateIsNull(bookId)).thenReturn(Optional.of(borrowingRecord))
        whenever(bookRepository.save(any<Book>())).thenReturn(borrowedBook)
        whenever(borrowingRecordRepository.save(any<BorrowingRecord>())).thenAnswer { it.getArgument(0) }

        bookService.returnBook(bookId)

        verify(borrowingRecordRepository).save(borrowingRecordCaptor.capture())
        val savedRecord = borrowingRecordCaptor.value
        assertEquals(0.0, savedRecord.lateFee)
        assertEquals(returnDate, savedRecord.returnDate)
    }

    @Test
    fun `borrowBook should throw BorrowingLimitExceededException when user is at borrowing limit`() {
        val bookId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val borrowingLimit = 5L
        val bookToBorrow = Book(id = bookId, title = "Borrow Me", author = "Author", available = true)
        val userBorrowing = User(id = userId, name = "Borrower", email = "borrow@test.com")

        whenever(bookRepository.findById(bookId)).thenReturn(Optional.of(bookToBorrow))
        whenever(userRepository.findById(userId)).thenReturn(Optional.of(userBorrowing))
        whenever(borrowingRecordRepository.countByUserIdAndReturnDateIsNull(userId)).thenReturn(borrowingLimit)

        val exception = assertThrows<BorrowingLimitExceededException> {
            bookService.borrowBook(bookId, userId)
        }
        assertEquals("User with ID $userId has reached the borrowing limit of $borrowingLimit books.", exception.message)

        verify(bookRepository).findById(bookId)
        verify(userRepository).findById(userId)
        verify(borrowingRecordRepository).countByUserIdAndReturnDateIsNull(userId)
        verify(bookRepository, never()).save(any<Book>())
        verify(borrowingRecordRepository, never()).save(any<BorrowingRecord>())
    }


    // todo:
    // - Concurrency: Test simultaneous borrowing attempts on the last available book (likely requires integration tests).
    // - Search Edge Cases:
    //    - Test search with empty strings or null parameters.
    //    - Test search returning no results.
    //    - Test search with special characters (e.g., apostrophes, hyphens).
    // - Update Scenarios:
    //    - Test updating only specific fields of a book.
    //    - Test business rules, e.g., should updating a book's title/author be allowed if it's currently borrowed?
    //    - Test trying to mark a book as 'available' via update if it has an active borrowing record.
    // - Borrowing/Returning Edge Cases:
    //    - Test returning a book when the associated user no longer exists.
    //    - Test scenarios where `dueDate` might be null unexpectedly (if possible).
    // - Data Validation:
    //    - Test adding/updating books with invalid data (e.g., empty title/author) if service layer performs validation.
    // - Idempotency:
    //    - Test adding a book with details identical to an existing one (should it be allowed or throw an error?).

}
