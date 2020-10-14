package qlik.qliktest.controller

import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import qlik.qliktest.domain.Message
import qlik.qliktest.dto.MessageRS
import qlik.qliktest.dto.Request
import qlik.qliktest.exception.MessageAlreadyExistsException
import qlik.qliktest.exception.MessageNotFoundException
import qlik.qliktest.log.LoggingContext
import qlik.qliktest.service.MessageService
import qlik.qliktest.validator.RequestValidator
import javax.validation.Valid

@RestController
@RequestMapping("/messages")
class MessageManagerController(val messageService: MessageService, val requestValidator: RequestValidator) {

    @GetMapping
    @ApiOperation(value = "Retrieves all existing messages")
    fun getMessages(): List<MessageRS> {
        LoggingContext.controllerName = "MessageManagerController"
        LoggingContext.controllerMethodName = "getMessages"

        return messageService.findAll().map { MessageRS(it.id, it.text, it.isPalindrome!!) }
    }

    @GetMapping("/{messageId}")
    @ApiOperation(value = "Retrieve an existing message by messageId")
    fun getMessage(@ApiParam("messageId to search by ") @PathVariable messageId: String): MessageRS {
        LoggingContext.controllerName = "MessageManagerController"
        LoggingContext.controllerMethodName = "getMessage"

        return messageService.findById(messageId)?.let {
            return MessageRS(it.id, it.text, it.isPalindrome!!)
        } ?: throw MessageNotFoundException("$messageId not found")
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(NO_CONTENT)
    @ApiOperation(value = "Delete an existing message by messageId")
    fun deleteMessage(@ApiParam("messageId to delete by") @PathVariable messageId: String) {
        LoggingContext.controllerName = "MessageManagerController"
        LoggingContext.controllerMethodName = "deleteMessage"

        messageService.findById((messageId))?.let {
            messageService.delete(messageId)
        } ?: throw MessageNotFoundException("$messageId not found")
    }

    @PutMapping
    @ResponseStatus(NO_CONTENT)
    @ApiOperation(value = "update an existing message")
    fun updateMessage(@RequestBody request: Request) {
        LoggingContext.controllerName = "MessageManagerController"
        LoggingContext.controllerMethodName = "putMessage"

        requestValidator.validate(request)

        messageService.findById(request.messageId)?.let {
            messageService.save(Message(request.messageId, request.text))
        } ?: throw MessageNotFoundException("${request.messageId} not found")
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @ApiOperation(value = "create a new message")
    fun createMessage(@RequestBody @Valid request: Request) {
        LoggingContext.controllerName = "MessageManagerController"
        LoggingContext.controllerMethodName = "createMessage"

        requestValidator.validate(request)

        if (messageService.findById(request.messageId) == null) {
            messageService.save(Message(request.messageId, request.text))
        } else {
            throw MessageAlreadyExistsException("${request.messageId} already exists")
        }
    }
}
