package ch.chrigu.gmf.givemefeatures.shared.history

interface Snapshot<S : Snapshot<S>> {
    fun withVersion(version: Long): S
    val version: Long
}
