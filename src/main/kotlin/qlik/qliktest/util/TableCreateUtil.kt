package qlik.qliktest.util

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException
import mu.KotlinLogging
import kotlin.reflect.KClass

object TableCreateUtil {

    private val logger = KotlinLogging.logger { }

    fun createTableForEntity(amazonDynamoDB: AmazonDynamoDB, entity: KClass<*>) {
        val tableRequest = DynamoDBMapper(amazonDynamoDB)
            .generateCreateTableRequest(entity.java)
            .withProvisionedThroughput(ProvisionedThroughput(1L, 1L))

        try {
            DynamoDB(amazonDynamoDB).createTable(tableRequest).waitForActive()
        } catch (e: ResourceInUseException) {
            logger.info("Table already exists - skip creation! [entity={}]", entity)
        }
    }
}
