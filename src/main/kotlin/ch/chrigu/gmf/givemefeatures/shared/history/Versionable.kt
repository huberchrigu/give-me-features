package ch.chrigu.gmf.givemefeatures.shared.history

import ch.chrigu.gmf.givemefeatures.shared.AggregateRoot

interface Versionable<S : Snapshot<S>, T : Versionable<S, T, ID>, ID> : AggregateRoot<ID> {
    val version: Long?
    val history: History<S>

    fun getCurrent(): S

    fun withHistory(history: History<S>): T

    fun withSnapshot(snapshot: S): T

    fun getSnapshot(version: Long) = history[version]

    fun newVersion(newVersion: T) = newVersion.withHistory(appendCurrentToHistory())

    fun appendCurrentToHistory() = history.add(getCurrent())

    fun getVersion(version: Long): T = if (version == this.version)
        this as T
    else
        revertTo(version)

    fun revertTo(snapshot: S) = withSnapshot(snapshot).withHistory(history.before(snapshot.version))

    private fun revertTo(version: Long) = revertTo(history[version])
}
