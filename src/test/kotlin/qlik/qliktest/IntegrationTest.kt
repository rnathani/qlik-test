package qlik.qliktest

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.RestAssured
import io.restassured.response.ValidatableResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import qlik.qliktest.dto.ErrorRS
import qlik.qliktest.dto.MessageRS
import qlik.qliktest.dto.Request
import qlik.qliktest.repository.MessageRepository

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [Application::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [DockerDynamoDBInitializer::class])
@ActiveProfiles("it")
class IntegrationTest {

    private val basePath = "messages"

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var messageRepository: MessageRepository

    @LocalServerPort
    private val port: Int = 0

    @Before
    fun before() {
        RestAssured.port = port
    }

    @Test
    fun `GIVEN empty DynamoDb WHEN GET endpoint is called THEN 404 status code is returned`() {
        try {
            RestAssured
                .given()
                .`when`()
                .basePath(basePath)
                .get()
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
        } finally {
            messageRepository.deleteAll()
        }
    }

    @Test
    fun `GIVEN DynamoDB with messages WHEN GET endpoint is THEN THEN 200 status code and messages is returned`() {
        try {
            givenMessageExists()
            val getResponse = RestAssured
                .given()
                .`when`()
                .basePath(basePath)
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .jsonPath()
                .getList(".", MessageRS::class.java)

            assertThat(getResponse).hasSize(1)
            assertThat(getResponse.get(0).isPalindrome).isTrue()
            assertThat(getResponse.get(0).id).isEqualTo("test1")
            assertThat(getResponse.get(0).message).isEqualTo("racecar")
        } finally {
            messageRepository.deleteAll()
        }
    }

    @Test
    fun `GIVEN DynamoDB with message When GET message is called for non-existing messageId THEN 404 status is returned`() {
        try {
            givenMessageExists()
            RestAssured
                .given()
                .`when`()
                .basePath(basePath)
                .get("/{messageId}", "test234")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
        } finally {
            messageRepository.deleteAll()
        }
    }

    @Test
    fun `GIVEN DynamoDB with nonPalindromic message When GET is called with messageId THEN 200 status code is returned`() {
        try {
            givenMessageExists(message = "test")
            val response = RestAssured
                .given()
                .`when`()
                .basePath(basePath)
                .get("/{messageId}", "test1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .`as`(MessageRS::class.java)

            assertThat(response.id).isEqualTo("test1")
            assertThat(response.message).isEqualTo("test")
            assertThat(response.isPalindrome).isFalse()
        } finally {
            messageRepository.deleteAll()
        }
    }

    @Test
    fun `GIVEN a existing message WHEN PUT is called THEN 204 status code is returned`() {
        try {
            givenMessageExists()
            RestAssured
                .given()
                .`when`()
                .basePath(basePath)
                .body(buildMessage("test1", "non-palindrome"))
                .contentType("Application/json")
                .put()
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

            val response = RestAssured
                .given()
                .`when`()
                .basePath(basePath)
                .get("/{messageId}", "test1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .body()
                .`as`(MessageRS::class.java)

            assertThat(response.id).isEqualTo("test1")
            assertThat(response.message).isEqualTo("non-palindrome")
            assertThat(response.isPalindrome).isFalse()
        } finally {
            messageRepository.deleteAll()
        }
    }

    @Test
    fun `GIVEN a non existing message WHEN PUT is called THEN 404 status code is returned`() {
        RestAssured
            .given()
            .`when`()
            .basePath(basePath)
            .body(buildMessage("test1", text = "test"))
            .contentType("Application/json")
            .put()
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .extract()
            .body()
            .`as`(ErrorRS::class.java)
    }

    @Test
    fun `GIVEN a existing message WHEN POST is called TWICE THEN 422 status code is returned`() {
        try {
            givenMessageExists()
            givenMessageExists(statusCode = HttpStatus.UNPROCESSABLE_ENTITY.value())
        } finally {
            messageRepository.deleteAll()
        }
    }

    @Test
    fun `GIVEN a existing message WHEN PUT is called THEN 422 status code is returned`() {
        try {
            givenMessageExists()
            givenMessageExists(statusCode = HttpStatus.UNPROCESSABLE_ENTITY.value())
        } finally {
            messageRepository.deleteAll()
        }
    }

    @Test
    fun `GIVEN a non existing message WHEN DELETE is called THEN 404 status code is returned`() {
        try {
            RestAssured
                .given()
                .`when`()
                .basePath(basePath)
                .delete("/{messageId}", "test1")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .extract()
                .body()
        } finally {
            messageRepository.deleteAll()
        }
    }

    @Test
    fun `GIVEN a existing message WHEN DELETE is called THEN 204 status code is returned`() {
        try {
            givenMessageExists()
            RestAssured
                .given()
                .`when`()
                .basePath(basePath)
                .delete("/{messageId}", "test1")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract()
                .body()
        } finally {
            messageRepository.deleteAll()
        }
    }

    @Test
    fun `GIVEN empty messageId WHEN POST is called THEN 400 status code is returned with error`() {
        val errorRS = givenMessageExists(messageId = "", statusCode = HttpStatus.BAD_REQUEST.value())
            .extract()
            .body()
            .`as`(ErrorRS::class.java)

        assertThat(errorRS.message?.equals("MessageId cannot be empty or null"))
    }

    @Test
    fun `GIVEN null message WHEN POST is called THEN 400 status code is returned with error`() {
        RestAssured
            .given()
            .`when`()
            .basePath(basePath)
            .body("{\\\"text\\\" : \\\"racecar\\\"}")
            .contentType("Application/json")
            .put()
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    private fun buildMessage(messageId: String = "test1", text: String = "racecar"): String {
        val req = Request(messageId, text)
        return mapper.writeValueAsString(req)
    }

    private fun givenMessageExists(messageId: String = "test1", message: String = "racecar", statusCode: Int = HttpStatus.CREATED.value()): ValidatableResponse {
        return RestAssured
            .given()
            .`when`()
            .basePath(basePath)
            .body(buildMessage(messageId, message))
            .contentType("Application/json")
            .post()
            .then()
            .statusCode(statusCode)
    }
}
