package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        database.close()
        stopKoin()
    }

    @Test
    fun insertReminderAndGetById() = runTest {
        val data = ReminderDTO("title", "description", "location", 0.0, 0.0, "id")
        database.reminderDao().saveReminder(data)

        // WHEN - Get the task by id from the database.
        val reminder = database.reminderDao().getReminderById(data.id)

        // THEN - The loaded data contains the expected values.
        assertThat<ReminderDTO>(reminder as ReminderDTO, notNullValue())
        assertThat(reminder.id, `is`(data.id))
        assertThat(reminder.title, `is`(data.title))
        assertThat(reminder.description, `is`(data.description))
        assertThat(reminder.location, `is`(data.location))
        assertThat(reminder.longitude, `is`(data.longitude))
        assertThat(reminder.latitude, `is`(data.latitude))
    }

    @Test
    fun insertRemindersAndGetFullList() = runTest {
        database.reminderDao()
            .saveReminder(ReminderDTO("title1", "description1", "location1", 0.0, 0.0, "id1"))
        database.reminderDao()
            .saveReminder(ReminderDTO("title2", "description2", "location2", 0.0, 0.0, "id2"))
        database.reminderDao()
            .saveReminder(ReminderDTO("title3", "description3", "location3", 0.0, 0.0, "id3"))

        val reminders = database.reminderDao().getReminders()
        assertThat(reminders.size, `is`(3))
    }

}