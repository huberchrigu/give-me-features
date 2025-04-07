package ch.chrigu.gmf.givemefeatures.shared.history

import ch.chrigu.gmf.givemefeatures.shared.AggregateRoot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HistoryTest {
    private val id = "abc"

    @Test
    fun `should make sure that version is correct`() {
        val testee = History(id).add(DummyAggregate(id, 0))
        val saved = testee.copy(version = 0)
        val newVersion = saved.add(DummyAggregate(id, 1))
        val newVersionSaved = newVersion.copy(version = 1)
        assertThat(newVersionSaved.find(0L)).isEqualTo(DummyAggregate(id, 0))
    }

    data class DummyAggregate(override val id: String?, override val version: Long?) : AggregateRoot<String> {
        override fun isNew(): Boolean {
            return false
        }
    }
}
