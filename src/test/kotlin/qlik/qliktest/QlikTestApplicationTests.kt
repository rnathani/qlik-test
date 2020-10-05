package qlik.qliktest

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(initializers = [DockerDynamoDBInitializer::class])
@ActiveProfiles("it")
class QlikTestApplicationTests {

    @Test
    fun contextLoads() {
    }
}
