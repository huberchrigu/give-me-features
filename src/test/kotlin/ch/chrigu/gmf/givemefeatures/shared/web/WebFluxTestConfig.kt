package ch.chrigu.gmf.givemefeatures.shared.web

import ch.chrigu.gmf.givemefeatures.shared.security.SecurityConfiguration
import ch.chrigu.gmf.givemefeatures.shared.web.advices.GlobalControllerAdvice
import ch.chrigu.gmf.givemefeatures.shared.web.advices.GlobalExceptionHandler
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveRepositoriesAutoConfiguration
import org.springframework.context.annotation.Import

@SpringBootConfiguration(proxyBeanMethods = false)
@EnableAutoConfiguration(
    exclude = [DataMongoReactiveRepositoriesAutoConfiguration::class],
    excludeName = [MODULITH_AUTO_CONFIG, MODULE_OBSERVABILITY_AUTO_CONFIG]
)
@Import(SecurityConfiguration::class, GlobalExceptionHandler::class, GlobalControllerAdvice::class)
class WebFluxTestConfig

private const val MODULITH_AUTO_CONFIG = "org.springframework.modulith.runtime.autoconfigure.SpringModulithRuntimeAutoConfiguration"
private const val MODULE_OBSERVABILITY_AUTO_CONFIG = "org.springframework.modulith.observability.autoconfigure.ModuleObservabilityAutoConfiguration"
