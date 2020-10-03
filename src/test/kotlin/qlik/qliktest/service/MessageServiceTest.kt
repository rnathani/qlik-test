package qlik.qliktest.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import qlik.qliktest.checker.PalindromeChecker
import qlik.qliktest.domain.Message
import qlik.qliktest.entity.MessageEntity
import qlik.qliktest.repository.MessageRepository
import java.util.Optional

class MessageServiceTest {

    private val repositoryMock = mockk<MessageRepository>()
    private val palindromeCheckerMock = mockk<PalindromeChecker>()
    private val sut = MessageService(palindromeCheckerMock, repositoryMock)

    @Test
    fun `GIVEN messages exists then findAll returns all the messages`() {
        val entity1 = MessageEntity()
        entity1.id = "entity1"
        entity1.message = "message1"
        entity1.isPalindrome = true

        val entity2 = MessageEntity()
        entity2.id = "entity2"
        entity2.message = "message2"
        entity2.isPalindrome = false

        val entity3 = MessageEntity()
        entity3.id = "entity3"
        entity3.message = "message3"
        entity3.isPalindrome = true

        every { repositoryMock.findAll() } returns listOf(entity1, entity2, entity3)

        val messages = sut.findAll()
        assertThat(messages).hasSize(3)
    }

    @Test
    fun `GIVEN no messages exists then findAll returns emptylist`() {
        every { repositoryMock.findAll() } returns emptyList()

        val messages = sut.findAll()
        assertThat(messages).isEmpty()
    }

    @Test
    fun `GIVEN a non existing messageID then find returns null`() {
        every { repositoryMock.findById(any()) } returns Optional.empty()
        assertThat(sut.findById("test123")).isNull()
    }

    @Test
    fun `GIVEN a existing messageID then find returns the Message`() {
        val entity = MessageEntity()
        entity.id = "Test123"
        entity.message = "test"
        entity.isPalindrome = false

        every { repositoryMock.findById(any()) } returns Optional.of(entity)

        assertThat(sut.findById("test123")).isEqualTo(Message("Test123", "test", false))
    }

    @Test
    fun `GIVEN a messageID WHEN delete is called THEN repository's delete method is called`() {
        val messageId = "test123"
        every { repositoryMock.deleteById(messageId) } returns Unit

        sut.delete(messageId)

        verify(exactly = 1) { repositoryMock.deleteById(messageId) }
    }

    @Test
    fun `GIVEN a message WHEN save is called THEN repository's save method is called with MessageEntity`() {
        val entitySlot = slot<MessageEntity>()
        val textSlot = slot<String>()

        val sut = MessageService(palindromeCheckerMock, repositoryMock)

        every { repositoryMock.save(capture(entitySlot)) } returns MessageEntity()
        every { palindromeCheckerMock.check(capture(textSlot)) } returns true

        val message = Message("test123", "test")

        sut.save(message)

        verify(exactly = 1) { repositoryMock.save(any<MessageEntity>()) }
        verify(exactly = 1) { palindromeCheckerMock.check(any()) }

        assertThat(entitySlot.isCaptured).isTrue()

        val entity = entitySlot.captured
        assertThat(entity.id).isEqualTo("test123")
        assertThat(entity.message).isEqualTo("test")
        assertThat(entity.isPalindrome).isTrue()
    }
}