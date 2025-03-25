package ch.chrigu.gmf.givemefeatures.shared.history

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class HistoryTest {
    @Test
    fun `should merge histories`() {
        val testee = History<TestSnapshot>().add(listOf(TestSnapshot("branch1", 0), TestSnapshot("branch1", 1)))
        val result = testee.mergeWith(History<TestSnapshot>().add(TestSnapshot("branch2", 0)), History())
        assertThat(result).isEqualTo(
            History(
                TreeMap(
                    mapOf(
                        0L to TestSnapshot("branch1", 0),
                        1L to TestSnapshot("branch1", 1),
                        2L to TestSnapshot("branch2", 2)
                    )
                )
            )
        )
    }

    data class TestSnapshot(val name: String, override val version: Long) : Snapshot<TestSnapshot> {
        override fun withVersion(version: Long): TestSnapshot {
            return copy(version = version)
        }
    }
}
