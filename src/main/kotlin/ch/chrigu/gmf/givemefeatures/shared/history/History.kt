package ch.chrigu.gmf.givemefeatures.shared.history

import ch.chrigu.gmf.givemefeatures.shared.aggregates.AggregateRoot
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.FieldType
import org.springframework.data.mongodb.core.mapping.MongoId
import java.util.*

data class History<T : AggregateRoot<ID>, ID>(
    @field:MongoId(targetType = FieldType.STRING) private val id: ID,
    @field:Version private val version: Long?,
    private val snapshots: SortedMap<Long, T> = sortedMapOf()
) {
    constructor(id: ID, vararg snapshots: T) : this(id, null, TreeMap(snapshots.associateBy { it.version }))

    init {
        if (snapshots.isNotEmpty()) {
            require(snapshots.keys == (0L..<snapshots.size).toSet()) { "Expected keys from 0 to ${snapshots.size - 1}, but has ${snapshots.keys}" }
            val ids = snapshots.values.map { it.id }
            require(ids.all { it == ids.first() })
        }
        if (version == null) {
            require(snapshots.size <= 1) { "New history may have max. one snapshot, but there are ${snapshots.size}" }
        } else if (isSaved()) {
            require(snapshots.size.toLong() == version + 1L)
        } else {
            require(snapshots.size.toLong() == version + 2L) { "History was not saved yet and has ${snapshots.size} snapshots, but expected ${version + 2}" }
        }
    }

    fun add(snapshot: T): History<T, ID> = copy(snapshots = TreeMap(snapshots + (snapshot.version to snapshot)))

    operator fun get(version: Long) =
        snapshots[version] ?: throw IllegalArgumentException("No version $version available")

    fun find(version: Long) = snapshots[version]

    fun add(snapshot: List<T>) = copy(snapshots = TreeMap(snapshots + snapshot.associateBy { it.version }))

    private fun isSaved() = version != null && version == snapshots.size.toLong() - 1L
}
