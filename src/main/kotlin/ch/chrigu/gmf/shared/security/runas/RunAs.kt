package ch.chrigu.gmf.shared.security.runas

import kotlinx.coroutines.reactor.asCoroutineContext
import kotlinx.coroutines.withContext
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder

object RunAs {
    suspend fun <T> runAs(role: String, block: suspend () -> T): T {
        val context = ReactiveSecurityContextHolder.withAuthentication(
            UsernamePasswordAuthenticationToken.authenticated("Internal user elevation", null, listOf(SimpleGrantedAuthority("ROLE_$role")))
        )
        return withContext(context.asCoroutineContext()) { block() }
    }
}