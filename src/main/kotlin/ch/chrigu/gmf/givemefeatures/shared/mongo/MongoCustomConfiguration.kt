package ch.chrigu.gmf.givemefeatures.shared.mongo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

@Configuration
class MongoCustomConfiguration {
    @Bean
    fun customConversions() = MongoCustomConversions(
        listOf(
            StringToHtmlConverter(), HtmlToStringConverter()
        )
    )
}
