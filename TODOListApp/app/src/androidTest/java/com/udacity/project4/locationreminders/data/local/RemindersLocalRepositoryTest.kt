package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveReminder_retrievesReminder() = runBlocking {
        val newReminder = ReminderDTO("title", "description", "location", 0.0, 0.0, "id")
        localDataSource.saveReminder(newReminder)

        val result = localDataSource.getReminder(newReminder.id)

        result as Success
        assertThat(result.data.title, `is`("title"))
        assertThat(result.data.description, `is`("description"))
        assertThat(result.data.location, `is`("location"))
        assertThat(result.data.latitude, `is`(0.0))
        assertThat(result.data.longitude, `is`(0.0))

    }

    @Test
    fun getReminders_retrievesAllSavedReminders() = runBlocking {
        localDataSource.saveReminder(ReminderDTO("title", "description", "location", 0.0, 0.0, "id1"))
        localDataSource.saveReminder(ReminderDTO("title", "description", "location", 0.0, 0.0, "id2"))
        localDataSource.saveReminder(ReminderDTO("title", "description", "location", 0.0, 0.0, "id3"))

        val result = localDataSource.getReminders()

        result as Success
        assertThat(result.data.size, `is`(3))
    }

    @Test
    fun deleteReminders_clearsAllReminders() = runBlocking {
        localDataSource.saveReminder(ReminderDTO("title", "description", "location", 0.0, 0.0, "id1"))
        localDataSource.saveReminder(ReminderDTO("title", "description", "location", 0.0, 0.0, "id2"))
        localDataSource.saveReminder(ReminderDTO("title", "description", "location", 0.0, 0.0, "id3"))

        localDataSource.deleteAllReminders()
        val result = localDataSource.getReminders()

        result as Success
        assertThat(result.data.size, `is`(0))
    }
}