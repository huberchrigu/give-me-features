package ch.chrigu.gmf.givemefeatures.shared.history

import java.util.*

data class History<S : Snapshot<S>>(private val snapshots: SortedMap<Long, S> = sortedMapOf()) {
    constructor(vararg snapshots: S) : this(TreeMap(snapshots.associateBy { it.version }))

    init {
        if (snapshots.isNotEmpty()) {
            require(snapshots.keys == (0L..<snapshots.size).toSet()) { "Expected keys from 0 to ${snapshots.size - 1}, but has ${snapshots.keys}" }
        }
    }

    fun before(version: Long) = History(snapshots.headMap(version))
    fun add(snapshot: S) = History(TreeMap(snapshots + (snapshot.version to snapshot)))
    operator fun get(version: Long) = snapshots[version] ?: throw IllegalArgumentException("No version $version available")
    fun mergeWith(history: History<S>, commonVersions: History<S>): History<S> {
        val newSnapshots: Map<Long, S> = history.snapshots - commonVersions.snapshots.keys
        if (newSnapshots.isEmpty()) return this
        val latestVersion = snapshots.lastKey()
        val keyModifier: Long = latestVersion + 1L - newSnapshots.keys.first()
        return add(newSnapshots.map { entry -> entry.value.withVersion(entry.key + keyModifier) })
    }

    fun add(snapshot: List<S>) = History(TreeMap(snapshots + snapshot.associateBy { it.version }))
}
