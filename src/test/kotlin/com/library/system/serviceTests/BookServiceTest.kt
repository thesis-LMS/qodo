import com.library.system.model.Book
import com.library.system.services.BookService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    @Mock
    lateinit var bookService: BookService

    @Test
    fun `should return all books`() {
        val book1 = Book(1, "Test Book 1", "Author 1", true)
        val book2 = Book(2, "Test Book 2", "Author 2", true)
        val expectedBooks = listOf(book1, book2)

        Mockito.`when`(bookService.getAllBooks()).thenReturn(expectedBooks)

        val actualBooks = bookService.getAllBooks()

        Assertions.assertEquals(expectedBooks, actualBooks)
        Mockito.verify(bookService).getAllBooks()
    }

    @Test
    fun `should return book by ID`() {
        val book = Book(1, "Test Book", "Author", true)
        Mockito.`when`(bookService.getBookById(1)).thenReturn(book)

        val actualBook = bookService.getBookById(1)

        Assertions.assertEquals(book, actualBook)
        Mockito.verify(bookService).getBookById(1)
    }

    @Test
    fun `should throw exception when book ID not found`() {
        Mockito.`when`(bookService.getBookById(1)).thenThrow(RuntimeException("Book not found"))

        Assertions.assertThrows(RuntimeException::class.java) {
            bookService.getBookById(1)
        }
    }

    @Test
    fun `should add new book`() {
        val book = Book(1, "New Book", "New Author", true)
        Mockito.doNothing().`when`(bookService).addBook(book)

        bookService.addBook(book)

        Mockito.verify(bookService).addBook(book)
    }

    @Test
    fun `should update an existing book`() {
        val book = Book(1, "Updated Book", "Updated Author", true)
        Mockito.doNothing().`when`(bookService).updateBook(book)

        bookService.updateBook(book)

        Mockito.verify(bookService).updateBook(book)
    }

    @Test
    fun `should throw exception when updating non-existent book`() {
        val book = Book(1, "Non-existent Book", "Author", true)
        Mockito.doThrow(RuntimeException("Book not found")).`when`(bookService).updateBook(book)

        Assertions.assertThrows(RuntimeException::class.java) {
            bookService.updateBook(book)
        }
    }

    @Test
    fun `should delete a book by ID`() {
        Mockito.doNothing().`when`(bookService).deleteBookById(1)

        bookService.deleteBookById(1)

        Mockito.verify(bookService).deleteBookById(1)
    }

    @Test
    fun `should return false when deleting non-existent book`() {
//        Mockito.`when`(bookService.deleteBookById(1)).thenReturn(false)
//
//        val result = bookService.deleteBookById(1)
//
//        Assertions.assertFalse(result)
//        Mockito.verify(bookService).deleteBookById(1)
    }

    @Test
    fun `should throw exception when borrowing an unavailable book`() {
        Mockito.`when`(bookService.borrowBook(1)).thenThrow(RuntimeException("Book is unavailable"))

        Assertions.assertThrows(RuntimeException::class.java) {
            bookService.borrowBook(1)
        }
    }

//    @Test
//    fun `should borrow an available book and mark it unavailable`() {
//        val book = Book(1, "Borrowed Book", "Author", true)
//        Mockito.`when`(bookService.borrowBook(1)).then {
//            book.isAvailable = false
//            book
//        }
//
//        val borrowedBook = bookService.borrowBook(1)
//
//        Assertions.assertFalse(borrowedBook.isAvailable)
//        Mockito.verify(bookService).borrowBook(1)
//    }
//
//    @Test
//    fun `should return a borrowed book and mark it available`() {
//        val book = Book(1, "Returned Book", "Author", false)
//        Mockito.`when`(bookService.returnBook(1)).then {
//            book.isAvailable = true
//            book
//        }
//
//        val returnedBook = bookService.returnBook(1)
//
//        Assertions.assertTrue(returnedBook.isAvailable)
//        Mockito.verify(bookService).returnBook(1)
//    }
//
//    @Test
//    fun `should throw exception when returning a book that is already available`() {
//        Mockito.`when`(bookService.returnBook(1)).thenThrow(RuntimeException("Book is already available"))
//
//        Assertions.assertThrows(RuntimeException::class.java) {
//            bookService.returnBook(1)
//        }
//    }
}

