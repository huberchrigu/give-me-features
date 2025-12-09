package ch.chrigu.gmf.architecture

import ch.chrigu.gmf.GiveMeFeaturesApplication
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter

class ArchitectureTest {
    private val applicationModules = ApplicationModules.of(GiveMeFeaturesApplication::class.java)

    @Test
    fun verifyArchitecture() {
        applicationModules.verify()
    }
    @Test
    fun document() {
        Documenter(applicationModules).writeDocumentation()
    }
}