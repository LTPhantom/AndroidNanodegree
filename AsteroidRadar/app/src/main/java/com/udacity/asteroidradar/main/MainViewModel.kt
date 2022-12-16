package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.asDomainObjects
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.asEntityObjects
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {
    private val database = getDatabase(application)
    val todaysAsteroids = database.asteroidDao.getTodaysAsteroids()

    init {
        viewModelScope.launch {
            val dummyAsteroids = listOf(
                Asteroid(1L, "Codename1", "2015-09-08", 16.0, 100.0, 100.0, 2345.0, false),
                Asteroid(2L, "Codename2", "2016-03-23", 54.34, 345.45, 434.4, 7643.0, true),
                Asteroid(3L, "Codename3", "2017-01-12", 68.1233, 45.2, 782.2, 89767.0, false),
                Asteroid(4L, "Codename4", "2018-04-05", 8.543, 876.0, 85.99, 234.0, true),
                Asteroid(5L, "Codename5", "2019-12-09", 234.2, 456.2, 67.32, 6325.0, false),
                Asteroid(6L, "Codename6", "2020-01-12", 98.76, 4.653, 3.23, 564.0, false),
                Asteroid(7L, "Codename7", "2021-12-18", 8678.3, 4523.435, 10000.1000, 1.0, true),
                Asteroid(8L, "Codename8", "2022-10-22", 34.55, 23.3, 35.5, 78635.0, true),
            )
            database.asteroidDao.insertAsteroids(*dummyAsteroids.asEntityObjects())
        }
    }

    class Factory(val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Invalid ViewModel")
        }
    }
}