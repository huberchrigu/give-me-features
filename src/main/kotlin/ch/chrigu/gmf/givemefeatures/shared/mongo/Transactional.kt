package ch.chrigu.gmf.givemefeatures.shared.mongo

import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.transaction.reactive.TransactionalOperator

/**
 * Retry transaction if it does not work.
 */
suspend fun <T : Any> TransactionalOperator.transactional(dbOperations: suspend () -> T): T = transactional(mono { dbOperations() })
    .retry(3)
    .awaitSingle()