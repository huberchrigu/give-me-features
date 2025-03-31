package ch.chrigu.gmf.givemefeatures.shared.mongo

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition

suspend fun <T> ReactiveTransactionManager.transactional(dbOperations: suspend () -> T): T {
    val transactionDefinition = DefaultTransactionDefinition()
    val transaction = getReactiveTransaction(transactionDefinition).awaitSingle()

    try {
        val result = dbOperations()
        commit(transaction).awaitSingle()
        return result
    } catch (t: Throwable) {
        rollback(transaction).awaitSingle()
        throw t
    }
}