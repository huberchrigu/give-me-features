package ch.chrigu.gmf.givemefeatures.shared.history

interface WithHistory<S : Snapshot<S, T, ID>, T : WithHistory<S, T, ID>, ID> {
    val id: ID?
    val version: Long?
    val history: List<S>
    fun toSnapshot(): S
    fun withHistory(history: List<S>): T
    fun getMerger(base: T, newVersion: T, oldVersion: T): AbstractMerger<T, ID>

    fun getSnapshot(version: Long) = history.first { it.version == version }

    fun revertTo(snapshot: S) = snapshot.revertFrom(id!!, history.dropWhile { it.version >= snapshot.version })

    fun newVersion(newVersion: T) = newVersion.withHistory(appendCurrentToHistory())

    /**
     * Use it like this:
     * ```
     * repository.findById(id).mergeWith(newVersion)
     * ```
     * Then, if the repository version has the same version number as `newVersion`, keep the `newVersion`.
     * Otherwise, try to merge them based on history version for version number `newVersion.version`.
     */
    fun mergeWith(newVersion: T): T {
        val oldVersion = this as T
        require(newVersion.version != null && oldVersion.version != null) { "Can only merge versioned tasks" }
        require(newVersion.version!! <= oldVersion.version!!) { "New version $newVersion should be equal or smaller than older version $oldVersion.version" }
        if (newVersion.version == oldVersion.version) return newVersion
        val base = getSnapshot(newVersion.version!!)
        return getMerger(revertTo(base), newVersion, oldVersion).merge()
    }

    private fun appendCurrentToHistory() = listOf(toSnapshot()) + history
}

interface Snapshot<S : Snapshot<S, T, ID>, T, ID> {
    val version: Long
    fun revertFrom(id: ID, history: List<S>): T
}
