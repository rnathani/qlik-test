package qlik.qliktest

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class DockerDynamoDBInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {

    private val dynamoDBImage = "amazon/dynamodb-local:1.11.477"
    private val dynamoDBPort = 8000

    fun dynamoDBContainer(): GenericContainer<*> = GenericContainer<Nothing>(DockerImageName.parse(dynamoDBImage)).apply {
        withExposedPorts(dynamoDBPort)
        start()
    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val endpoint = with(dynamoDBContainer()) { "http://${getContainerIpAddress()}:${getMappedPort(dynamoDBPort)}" }
        TestPropertyValues.of("amazon.dynamodb.endpoint=$endpoint").applyTo(applicationContext)
    }
}
