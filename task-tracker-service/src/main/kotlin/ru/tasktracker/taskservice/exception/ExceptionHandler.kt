package ru.tasktracker.taskservice.exception

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime


data class AuthenticationException(override val message: String): RuntimeException(message)
data class UserException(override val message: String): RuntimeException(message)
data class TaskException(override val message: String): RuntimeException(message)
data class TaskGroupException(override val message: String): RuntimeException(message)

@ControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler
    fun handlerAllException(e: Exception, request: WebRequest): ResponseEntity<ExceptionResponse> {
        val response = ExceptionResponse(
            message = e.message ?: "Внутренняя ошибка",
            path = (request as ServletWebRequest).request.requestURI
        )
        log.error(response.toString(), e)
        return ResponseEntity.internalServerError().body(response)
    }

}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ExceptionResponse(
    val code: Int = HttpStatus.INTERNAL_SERVER_ERROR.value(),
    val status: String = HttpStatus.INTERNAL_SERVER_ERROR.name,
    val message: String,
    val path: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now()
)