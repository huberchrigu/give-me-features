package ch.chrigu.gmf

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.modulith.Modulithic

@SpringBootApplication
@Modulithic(sharedModules = ["shared"])
class GiveMeFeaturesApplication

fun main(args: Array<String>) {
    runApplication<GiveMeFeaturesApplication>(*args)
}
