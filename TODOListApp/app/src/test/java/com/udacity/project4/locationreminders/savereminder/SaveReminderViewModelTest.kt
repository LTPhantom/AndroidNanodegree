package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel
    private lateinit var dataSource: FakeDataSource

    @Before
    fun setUp() {
        val appContext = ApplicationProvider.getApplicationContext<Application>()
        dataSource = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(appContext, dataSource)
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun validateEnteredData_returnsFalseWhenTitleIsNull() {
        val data = ReminderDataItem(null, "description", "location", 0.0, 0.0)
        val returnValue = saveReminderViewModel.validateEnteredData(data)
        assertThat(returnValue, `is`(false))
    }

    @Test
    fun validateEnteredData_returnsFalseWhenTitleIsEmpty() {
        val data = ReminderDataItem("", "description", "location", 0.0, 0.0)
        val returnValue = saveReminderViewModel.validateEnteredData(data)
        assertThat(returnValue, `is`(false))
    }

    @Test
    fun validateEnteredData_returnsFalseWhenLocationIsNull() {
        val data = ReminderDataItem("title", "description", null, 0.0, 0.0)
        val returnValue = saveReminderViewModel.validateEnteredData(data)
        assertThat(returnValue, `is`(false))
    }

    @Test
    fun validateEnteredData_returnsFalseWhenLocationIsEmpty() {
        val data = ReminderDataItem("title", "description", "", 0.0, 0.0)
        val returnValue = saveReminderViewModel.validateEnteredData(data)
        assertThat(returnValue, `is`(false))
    }

    @Test
    fun saveReminder_savesReminderToRepository() = runTest {
        val data = ReminderDataItem("title", "description", "location", 0.0, 0.0, "id")
        saveReminderViewModel.saveReminder(data)
        when (val reminder = dataSource.getReminder("id")) {
            is Result.Success<*> -> {
                assertThat((reminder.data as ReminderDTO).title, equalTo("title"))
            }
            else -> fail("The returned reminder was an error.")
        }
    }

    @Test
    fun saveLocation_updatesLiveData() {
        saveReminderViewModel.saveLocationInformation("location", 10.5, 12.8, null)
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.value, `is`("location"))
        assertThat(saveReminderViewModel.longitude.value, `is`(10.5))
        assertThat(saveReminderViewModel.latitude.value, `is`(12.8))
    }

    @Test
    fun onClear_nullifiesAllValues() {
        saveReminderViewModel.saveLocationInformation("location", 10.5, 12.8, null)
        saveReminderViewModel.onClear()
        assertThat(saveReminderViewModel.reminderSelectedLocationStr.value, nullValue())
        assertThat(saveReminderViewModel.longitude.value, nullValue())
        assertThat(saveReminderViewModel.latitude.value, nullValue())
    }
}