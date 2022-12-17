package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.network.ImageOfTheDay
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {
    private val database = getDatabase(application)
    private val repository = AsteroidsRepository(database)
    val asteroids = repository.asteroids

    private var _imageOfTheDay = MutableLiveData<ImageOfTheDay?>()
    val imageOfTheDay: LiveData<ImageOfTheDay?>
        get() = _imageOfTheDay

    init {
        viewModelScope.launch {
            _imageOfTheDay.value = repository.getImageOfTheDay()
            repository.getAsteroids()
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Invalid ViewModel")
        }
    }
}