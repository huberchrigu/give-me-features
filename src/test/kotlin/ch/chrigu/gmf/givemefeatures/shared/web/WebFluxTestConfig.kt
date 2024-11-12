package ch.chrigu.gmf.givemefeatures.shared.web

import ch.chrigu.gmf.givemefeatures.shared.security.SecurityConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import

@TestConfiguration
@EnableAutoConfiguration(exclude = [MongoReactiveAutoConfiguration::class])
@Import(SecurityConfiguration::class, GlobalExceptionHandler::class)
class WebFluxTestConfig
