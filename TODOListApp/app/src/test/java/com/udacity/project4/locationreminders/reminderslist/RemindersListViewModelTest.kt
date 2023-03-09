package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var remindersViewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

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
    fun loadReminders_showNoData_isTrueWhenRemindersListIsEmpty() = runTest {
        //Given the initial state
        //When loadReminders is called with the dataSource list empty
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

    @Test
    fun getReminder_returnsAnError() {
        dataSource.setReturnError(true)
        remindersViewModel.loadReminders()

        assertThat(remindersViewModel.showSnackBar.value,`is`("Test Error"))
    }

    @Test
    fun loadReminders_checkLoading() {
        mainCoroutineRule.pauseDispatcher()

        remindersViewModel.loadReminders()

        assertThat(remindersViewModel.showLoading.value, `is`(true))

        mainCoroutineRule.resumeDispatcher()

        assertThat(remindersViewModel.showLoading.value, `is`(false))
    }
}