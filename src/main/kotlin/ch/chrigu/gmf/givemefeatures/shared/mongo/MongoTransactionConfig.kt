package ch.chrigu.gmf.givemefeatures.shared.mongo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator

@Configuration
class MongoTransactionConfig {
    @Bean
    fun mongoTransactionManager(databaseFactory: ReactiveMongoDatabaseFactory) = ReactiveMongoTransactionManager(databaseFactory)

    @Bean
    fun transactionalOperator(transactionManager: ReactiveTransactionManager) = TransactionalOperator.create(transactionManager)
}
