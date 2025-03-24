package ch.chrigu.gmf.givemefeatures

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<GiveMeFeaturesApplication>().with(TestcontainersConfiguration::class, TestDataGeneratorConfiguration::class).run(*args)
}
