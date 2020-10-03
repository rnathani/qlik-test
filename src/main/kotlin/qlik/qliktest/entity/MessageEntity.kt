package qlik.qliktest.entity

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable

@DynamoDBTable(tableName = "message")
class MessageEntity {

    @DynamoDBHashKey(attributeName = "id")
    var id: String? = null

    @DynamoDBAttribute
    var message: String? = null

    @DynamoDBAttribute
    var isPalindrome: Boolean? = null
}
