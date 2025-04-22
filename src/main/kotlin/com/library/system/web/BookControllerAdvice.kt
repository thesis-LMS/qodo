import com.library.system.model.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BookControllerAdvice {
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(e: ResourceNotFoundException) = ResponseEntity.notFound().build<Void>()

    @ExceptionHandler(BookNotAvailableException::class)
    fun handleNotAvailable(e: BookNotAvailableException) = ResponseEntity.status(HttpStatus.CONFLICT).build<Void>()

    @ExceptionHandler(BookAlreadyReturnedException::class)
    fun handleAlreadyReturned(e: BookAlreadyReturnedException) = ResponseEntity.status(HttpStatus.CONFLICT).build<Void>()
}