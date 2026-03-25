package ch.chrigu.gmf.plugins.jobs

import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BackgroundJobConfiguration {
    private val job = SupervisorJob()

    @Bean
    fun backgroundCoroutine() = CoroutineScope(Dispatchers.Default + job)

    @PreDestroy
    fun cancelJob() = job.cancel()
}