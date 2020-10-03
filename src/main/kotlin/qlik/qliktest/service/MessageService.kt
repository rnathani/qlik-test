package qlik.qliktest.service

import qlik.qliktest.checker.Checker
import qlik.qliktest.checker.PalindromeChecker
import qlik.qliktest.domain.Message
import qlik.qliktest.entity.MessageEntity
import qlik.qliktest.repository.MessageRepository

class MessageService(val palindromeChecker: Checker = PalindromeChecker(), val messageRepository: MessageRepository) {

    fun findAll(): List<Message> {
        return messageRepository.findAll().map { toMessage(it) }
    }

    fun findById(messageId: String): Message? {
        return messageRepository.findById(messageId).map { toMessage(it) }.orElse(null)
    }

    fun delete(messageId: String) {
        messageRepository.deleteById(messageId)
    }

    fun save(message: Message) {
        messageRepository.save(toEntity(message))
    }

    private fun toMessage(entity: MessageEntity) = Message(entity.id!!, entity.message!!, entity.isPalindrome)

    private fun toEntity(message: Message): MessageEntity {
        val entity = MessageEntity()
        entity.id = message.id
        entity.message = message.text
        entity.isPalindrome = palindromeChecker.check(message.text)
        return entity
    }
}
