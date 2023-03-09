package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var reminders: HashMap<String, ReminderDTO> = HashMap()
    private var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Test Error")
        }
        return Result.Success(reminders.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.put(reminder.id, reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Test Error")
        }
        val value = reminders[id] ?: return Result.Error("Message Error")
        return Result.Success(value)
    }

    override suspend fun deleteAllReminders() {
        reminders = HashMap()
    }

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }
}