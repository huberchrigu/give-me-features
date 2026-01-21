package ch.chrigu.gmf.shared.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationFailureHandler
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Configuration
class SecurityConfiguration {
    @Bean
    fun httpSecurityFilterChain(http: ServerHttpSecurity) = http {
        authorizeExchange {
            authorize(pathMatchers("/favicon.ico", "/styles/**", "/webjars/**", LOGIN_PAGE), permitAll)
            authorize(anyExchange, authenticated)
        }
        formLogin {
            authenticationEntryPoint = RedirectServerAuthenticationEntryPoint(LOGIN_PAGE).apply { setRedirectStrategy(HtmxRedirectStrategy()) }
            loginPage = LOGIN_PAGE
            requiresAuthenticationMatcher = pathMatchers(HttpMethod.POST, LOGIN_PAGE)
            authenticationFailureHandler = RedirectServerAuthenticationFailureHandler("$LOGIN_PAGE?error")
        }
    }

    @Bean
    fun userDetailsService() = MapReactiveUserDetailsService(user("user"), user("admin", "ADMIN"))

    private fun user(name: String, vararg roles: String) = User.withDefaultPasswordEncoder() // TODO: Real user management
        .username(name).password(name).roles(*(arrayOf("USER") + roles))
        .build()

    companion object {
        const val LOGIN_PAGE = "/login"
    }
}
