package com.example.powerlite

import com.example.powerlite.data.repository.HistoryRepository
import com.example.powerlite.model.OperationType
import com.example.powerlite.testutil.FakeSharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HistoryRepositoryTest {

    private lateinit var fakePrefs: FakeSharedPreferences
    private lateinit var repository: HistoryRepository

    @Before
    fun setup() {
        fakePrefs = FakeSharedPreferences()
        repository = HistoryRepository(context = null, prefs = fakePrefs)
    }

    @Test
    fun testHistorySave_singleEntry() {
        val list = repository.addHistory(OperationType.POWER, "5", "3")
        assertEquals(1, list.size)
        assertEquals(OperationType.POWER, list[0].operation)
        assertEquals("5", list[0].firstInput)
        assertEquals("3", list[0].secondInput)

        val retrieved = repository.getHistory()
        assertEquals(1, retrieved.size)
        assertEquals("5", retrieved[0].firstInput)
    }

    @Test
    fun testHistoryLimit_maxTen() {
        // Add 10 unique calculations
        for (i in 1..10) {
            repository.addHistory(OperationType.POWER, i.toString(), "2")
        }
        val tenItems = repository.getHistory()
        assertEquals(10, tenItems.size)
        // Latest is 10, oldest is 1
        assertEquals("10", tenItems.first().firstInput)
        assertEquals("1", tenItems.last().firstInput)

        // Add 11th calculation
        repository.addHistory(OperationType.POWER, "11", "2")
        val elevenItems = repository.getHistory()
        assertEquals(10, elevenItems.size)
        // 11 should be first, and oldest (1) should be evicted
        assertEquals("11", elevenItems.first().firstInput)
        assertEquals("2", elevenItems.last().firstInput)
        assertTrue(elevenItems.none { it.firstInput == "1" })
    }

    @Test
    fun testConsecutiveDuplicateHistory_notAdded() {
        repository.addHistory(OperationType.POWER, "5", "3")
        assertEquals(1, repository.getHistory().size)

        // Add exact same again consecutively
        repository.addHistory(OperationType.POWER, "5", "3")
        assertEquals(1, repository.getHistory().size)

        // Add different calculation
        repository.addHistory(OperationType.ROOT, "125", "3")
        assertEquals(2, repository.getHistory().size)

        // Add 5, 3 again (now non-consecutive)
        repository.addHistory(OperationType.POWER, "5", "3")
        assertEquals(3, repository.getHistory().size)
    }

    @Test
    fun testHistoryDeletion_singleItem() {
        repository.addHistory(OperationType.POWER, "5", "3")
        repository.addHistory(OperationType.ROOT, "16", "2")
        val current = repository.getHistory()
        assertEquals(2, current.size)

        val idToDelete = current[0].id
        val remaining = repository.deleteHistory(idToDelete)
        assertEquals(1, remaining.size)
        assertEquals(current[1].id, remaining[0].id)
    }

    @Test
    fun testHistoryClear_all() {
        repository.addHistory(OperationType.POWER, "5", "3")
        repository.addHistory(OperationType.ROOT, "16", "2")
        assertEquals(2, repository.getHistory().size)

        repository.clearHistory()
        assertTrue(repository.getHistory().isEmpty())
    }
}
