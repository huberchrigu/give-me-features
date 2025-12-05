package ch.chrigu.gmf.givemefeatures.shared.web

import ch.chrigu.gmf.givemefeatures.shared.aggregates.AbstractAggregateRoot
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.reactive.result.view.Fragment

/**
 * Helper that generates a fragment for a given field update.
 * Requires a controller that handles PUT /[aggregateName]/{id}/merge?version={version} requests.
 *
 * @param aggregateName The first part of the resource URI.
 */
class UpdateFragmentBuilder<T : AbstractAggregateRoot<*>>(private val aggregateName: String, vararg fields: Pair<String, T.() -> Any>) {
    private val fields = fields.toList()

    fun toFragments(oldState: T, newState: T) = fields
        .map { (name, getter) -> updateFragment(name, oldState, newState, getter) }

    private fun updateFragment(fieldName: String, oldState: T, newState: T, getValue: T.() -> Any): ServerSentEvent<Fragment> {
        val newValue = newState.getValue().toString()
        val oldValue = oldState.getValue().toString()
        return ServerSentEvent.builder(
            Fragment.create(
                "atoms/updates", mapOf("fieldName" to fieldName) +
                        if (newValue == oldValue) emptyMap() else mapOf(
                            "update" to FieldUpdate(
                                "/$aggregateName/${oldState.id}$MERGE_URI?version=${oldState.version}", newState.version!!,
                                newValue
                            )
                        )
            )
        ).build()
    }

    companion object {
        const val MERGE_URI = "/merge"
    }
}