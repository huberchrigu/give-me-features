package ch.chrigu.gmf.givemefeatures.shared.history

interface Mergeable<S : Snapshot<S>, T : Mergeable<S, T, ID>, ID> : Versionable<S, T, ID> {
    fun getMerger(base: T, newVersion: T, oldVersion: T): AbstractMerger<S, T, ID>


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

    private fun revertTo(snapshot: S) = withSnapshot(snapshot).withHistory(history.before(snapshot.version))
}
