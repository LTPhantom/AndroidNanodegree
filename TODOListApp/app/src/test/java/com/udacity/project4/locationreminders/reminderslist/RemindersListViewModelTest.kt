package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersViewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setUp() {
        val appContext = ApplicationProvider.getApplicationContext<Application>()
        dataSource = FakeDataSource()
        remindersViewModel = RemindersListViewModel(appContext, dataSource)
    }

    @After
    fun cleanUp() {
        stopKoin()
        runBlocking { dataSource.deleteAllReminders() }
    }

    @Test
    fun loadReminders_showNoData_isTrueWhenRemindersListIsNull() = runTest {
        //Given the initial state
        //When loadReminders is called with the dataSource as null
        remindersViewModel.loadReminders()
        //Assert that showNoData is true
        assertThat(remindersViewModel.showNoData.value, `is`(true))
    }

    @Test
    fun loadReminders_showNoData_isTrueWhenRemindersListIsEmpty() = runTest {
        //Given the initial state
        //When loadReminders is called with the dataSource list empty
        dataSource.getReminders()
        remindersViewModel.loadReminders()
        //Assert that showNoData is true
        assertThat(remindersViewModel.showNoData.value, `is`(true))
    }

    @Test
    fun loadReminders_showNoData_isFalseWhenRemindersIsNotEmpty() = runTest {
        dataSource.saveReminder(ReminderDTO("testTitle",
            "testDescription",
            "testLocation",
            0.0,
            0.0))
        remindersViewModel.loadReminders()
        assertThat(remindersViewModel.showNoData.value, `is`(false))
    }

    @Test
    fun loadReminders_remindersList_isPopulated() = runTest {
        dataSource.saveReminder(ReminderDTO("testTitle",
            "testDescription",
            "testLocation",
            0.0,
            0.0))
        remindersViewModel.loadReminders()
        assertThat(remindersViewModel.remindersList.value?.get(0)?.title, equalTo("testTitle"))
    }
}