package qlik.qliktest.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties("amazon")
class AmazonProperties {

    @NestedConfigurationProperty
    var dynamoDB: DynamoDBProperties = DynamoDBProperties()

    @NestedConfigurationProperty
    var aws: AwsKeysProperties = AwsKeysProperties()
}
