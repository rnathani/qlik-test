package qlik.qliktest.config

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import qlik.qliktest.checker.Checker
import qlik.qliktest.checker.PalindromeChecker
import qlik.qliktest.entity.MessageEntity
import qlik.qliktest.repository.MessageRepository
import qlik.qliktest.service.MessageService
import qlik.qliktest.util.TableCreateUtil
import qlik.qliktest.validator.RequestValidator

@Configuration
@EnableConfigurationProperties(AmazonProperties::class)
@EnableDynamoDBRepositories(basePackages = ["qlik.qliktest.repository"])
class ApplicationConfig {

    @Bean
    fun amazonDynamoDB(amazonProperties: AmazonProperties): AmazonDynamoDB? {
        var amazonDynamoDB: AmazonDynamoDB
        if (amazonProperties.dynamoDB.embedded) {
            amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard()
                .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(amazonProperties.aws.accessKey, amazonProperties.aws.secretKey)))
                .withEndpointConfiguration(EndpointConfiguration(amazonProperties.dynamoDB.endpoint, ""))
                .build()
        } else {
            amazonDynamoDB = AmazonDynamoDBClientBuilder
                .standard().build()
        }

        TableCreateUtil.createTableForEntity(amazonDynamoDB, MessageEntity::class)
        return amazonDynamoDB
    }

    @Bean
    fun messageService(palindromeChecker: Checker, messageRepository: MessageRepository): MessageService {
        return MessageService(palindromeChecker, messageRepository)
    }

    @Bean
    fun palidomeChecker() = PalindromeChecker()

    @Bean
    fun requestValidator() = RequestValidator()
}
