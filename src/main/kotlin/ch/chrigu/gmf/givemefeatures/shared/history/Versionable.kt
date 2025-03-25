package ch.chrigu.gmf.givemefeatures.shared.history

import ch.chrigu.gmf.givemefeatures.shared.AggregateRoot

interface Versionable<S : Snapshot<S>, T : Versionable<S, T, ID>, ID> : AggregateRoot<ID> {
    val version: Long?
    val history: History<S>
    val current: S

    fun withHistory(history: History<S>): T
    fun withSnapshot(snapshot: S): T

    fun getSnapshot(version: Long) = history[version]
    fun newVersion(newVersion: T) = newVersion.withHistory(appendCurrentToHistory())

    fun appendCurrentToHistory() = history.add(current)
}
