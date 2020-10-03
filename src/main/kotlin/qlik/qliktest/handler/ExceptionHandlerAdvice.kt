package qlik.qliktest.handler

import mu.KotlinLogging
import org.apache.catalina.connector.ClientAbortException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import qlik.qliktest.controller.MessageManagerController
import qlik.qliktest.dto.ErrorRS
import qlik.qliktest.exception.BadRequestException
import qlik.qliktest.exception.MessageAlreadyExistsException
import qlik.qliktest.exception.MessageNotFoundException

@ControllerAdvice(basePackageClasses = [MessageManagerController::class])
class ExceptionHandlerAdvice {

    private val log = KotlinLogging.logger {}

    @ExceptionHandler(ClientAbortException::class)
    fun handleClientAbortedConnection(clientAbortException: ClientAbortException) {
        log.warn(clientAbortException) { "Connection dropped by client" }
    }

    @ExceptionHandler(Exception::class)
    fun exceptionHandler(ex: Exception): ResponseEntity<ErrorRS> {

        return when (ex) {
            is BadRequestException ->
                buildResponseEntity(status = HttpStatus.BAD_REQUEST, message = HttpStatus.BAD_REQUEST.reasonPhrase, error = ex.error)

            is HttpMessageNotReadableException ->
                buildResponseEntity(status = HttpStatus.BAD_REQUEST, message = HttpStatus.BAD_REQUEST.reasonPhrase, error = ex.message)

            is MessageNotFoundException ->
                buildResponseEntity(status = HttpStatus.NOT_FOUND, message = HttpStatus.NOT_FOUND.reasonPhrase, error = ex.error)

            is MessageAlreadyExistsException ->
                buildResponseEntity(status = HttpStatus.UNPROCESSABLE_ENTITY, message = HttpStatus.UNPROCESSABLE_ENTITY.reasonPhrase, error = ex.error)

            else -> {
                log.warn("Unhandled exception in controller", ex)
                buildResponseEntity(status = HttpStatus.INTERNAL_SERVER_ERROR, message = "Unexpected error occurred")
            }
        }
    }

    private fun buildResponseEntity(status: HttpStatus, message: String?, error: String? = null): ResponseEntity<ErrorRS> {
        return ResponseEntity
            .status(status)
            .body(ErrorRS(
                message = message,
                errors = error
            ))
    }
}
