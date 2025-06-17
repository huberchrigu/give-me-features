package ch.chrigu.gmf.givemefeatures.shared.history.merge

import ch.chrigu.gmf.givemefeatures.shared.aggregates.AbstractAggregateRoot
import ch.chrigu.gmf.givemefeatures.shared.history.AbstractMerger
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HistoryMergerTest {
    private val aggregateId = "1"
    private val originalName = "name"
    private val mergingVersion = DummyAggregate(aggregateId, 0, "name2")

    private val historyQuery = mockk<HistoryQuery<DummyAggregate, String>>()
    private val testee = HistoryMerger(DummyMerger(), historyQuery)

    @Test
    fun `should use new version because version number is the same`() = runTest {
        val result = mergeWith(0)
        assertThat(result).isEqualTo(mergingVersion)
    }

    @Test
    fun `should merge versions because persisted version number is greater`() = runTest {
        coEvery { historyQuery.getVersion(aggregateId, 0) } returns DummyAggregate(aggregateId, 0, originalName)
        val result = mergeWith(1)
        assertThat(result).isEqualTo(DummyAggregate(aggregateId, 1, "name2"))
    }

    private suspend fun mergeWith(persistedVersion: Long): DummyAggregate {
        val currentVersion = DummyAggregate(aggregateId, persistedVersion, originalName)
        return testee.merge(currentVersion, mergingVersion)
    }

    class DummyAggregate(id: String, version: Long, val name: String) : AbstractAggregateRoot<String>(id, version)
    class DummyMerger : AbstractMerger<DummyAggregate, String>() {
        override fun getMergedAggregate(id: String, version: Long, versions: MergingVersions<DummyAggregate>) = with(versions) {
            DummyAggregate(id, version, merge { name })
        }
    }
}
