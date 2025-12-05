package ch.chrigu.gmf.givemefeatures.shared.web

import ch.chrigu.gmf.givemefeatures.shared.security.SecurityConfiguration
import ch.chrigu.gmf.givemefeatures.shared.web.advices.GlobalControllerAdvice
import ch.chrigu.gmf.givemefeatures.shared.web.advices.GlobalExceptionHandler
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveRepositoriesAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import

@TestConfiguration(proxyBeanMethods = false)
@EnableAutoConfiguration(exclude = [DataMongoReactiveRepositoriesAutoConfiguration::class])
@Import(SecurityConfiguration::class, GlobalExceptionHandler::class, GlobalControllerAdvice::class)
class WebFluxTestConfig
