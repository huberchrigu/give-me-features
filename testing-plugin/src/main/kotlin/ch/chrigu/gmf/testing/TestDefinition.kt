package ch.chrigu.gmf.testing

import ch.chrigu.gmf.plugins.FeatureReferenceId
import ch.chrigu.gmf.plugins.TaskReferenceId
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId

data class TestDefinition(
    @MongoId(targetType = FieldType.STRING) val id: String,
    val tests: List<TestReference> = emptyList(),
    val state: TestState = TestState.NOT_STARTED
)

enum class TestState {
    NOT_STARTED, RUNNING, FINISHED
}

data class TestReference(val feature: FeatureReferenceId? = null, val task: TaskReferenceId? = null) {
    init {
        require((feature != null) xor (task != null)) { "Exactly one of feature or task must be set" }
    }
}