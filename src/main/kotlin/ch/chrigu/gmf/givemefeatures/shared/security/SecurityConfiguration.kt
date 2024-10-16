package ch.chrigu.gmf.givemefeatures.shared.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
class SecurityConfiguration {
    @Bean
    fun httpSecurityFilterChain(http: ServerHttpSecurity) = http {
        authorizeExchange {
            authorize(anyExchange, permitAll)
        }
    }
}