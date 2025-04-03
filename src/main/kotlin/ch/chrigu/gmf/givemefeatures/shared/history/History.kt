package ch.chrigu.gmf.givemefeatures.shared.history

import ch.chrigu.gmf.givemefeatures.shared.AggregateRoot
import org.springframework.data.annotation.Version
import java.util.*

data class History<T : AggregateRoot<ID>, ID>(
    private val id: ID,
    @field:Version private val version: Long?,
    private val snapshots: SortedMap<Long, T> = sortedMapOf()
) {
    constructor(id: ID, vararg snapshots: T) : this(id, null, TreeMap(snapshots.associateBy { it.version }))

    init {
        if (snapshots.isNotEmpty()) {
            require(snapshots.keys == (0L..<snapshots.size).toSet()) { "Expected keys from 0 to ${snapshots.size - 1}, but has ${snapshots.keys}" }
            val ids = snapshots.values.map { it.id }
            require(ids.size == ids.distinct().size)
        }
    }

    fun add(snapshot: T) = copy(snapshots = TreeMap(snapshots + (snapshot.version to snapshot)))

    operator fun get(version: Long) =
        snapshots[version] ?: throw IllegalArgumentException("No version $version available")

    fun find(version: Long) = snapshots[version]

    fun add(snapshot: List<T>) = copy(snapshots = TreeMap(snapshots + snapshot.associateBy { it.version }))
}
