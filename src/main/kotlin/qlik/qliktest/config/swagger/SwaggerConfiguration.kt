package qlik.qliktest.config.swagger

import com.google.common.base.Predicate
import com.google.common.base.Predicates
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@ConfigurationProperties(prefix = "swagger.service")
class SwaggerProperties {
    var version: String? = null
    var title: String? = null
    var description: String? = null
    var termsPath: String? = null
    var licenceType: String? = null
    var licencePath: String? = null
}

@EnableSwagger2
@EnableConfigurationProperties(SwaggerProperties::class)
@Configuration
class SwaggerConfiguration(val swaggerProperties: SwaggerProperties, val environment: Environment) {

    @Bean
    fun documentation(): Docket =
        Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(paths())
            .build()
            .pathMapping("/")
            .protocols(protocols())
            .apiInfo(apiInfo())

    private fun protocols(): MutableSet<String> =
        mutableSetOf<String>().apply { add("http") }

    private fun apiInfo(): ApiInfo =
        ApiInfoBuilder()
            .title(swaggerProperties.title)
            .description(swaggerProperties.description)
            .termsOfServiceUrl(swaggerProperties.termsPath)
            .license(swaggerProperties.licenceType)
            .licenseUrl(swaggerProperties.licencePath)
            .version(swaggerProperties.version)
            .build()

    private fun paths(): Predicate<String> = Predicates.or<String>(PathSelectors.regex("/messages.*"))
}
