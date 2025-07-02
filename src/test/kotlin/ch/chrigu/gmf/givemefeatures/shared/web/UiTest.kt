package ch.chrigu.gmf.givemefeatures.shared.web

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.TestConstructor
import kotlin.reflect.KClass

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(WebFluxTestConfig::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(SaveArtifactsOnFailure::class)
annotation class UiTest(@get:AliasFor(attribute = "classes", annotation = SpringBootTest::class) val value: KClass<*>)
