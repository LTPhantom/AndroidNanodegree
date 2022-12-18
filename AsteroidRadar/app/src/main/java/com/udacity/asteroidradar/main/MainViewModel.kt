package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.network.ImageOfTheDay
import com.udacity.asteroidradar.repository.AsteroidsFilter
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

enum class NasaApiStatus { LOADING, ERROR, DONE }
class MainViewModel(application: Application) : ViewModel() {
    private val database = getDatabase(application)
    private val repository = AsteroidsRepository(database)
    val asteroids = repository.asteroids

    private var _imageOfTheDay = MutableLiveData<ImageOfTheDay?>()
    val imageOfTheDay: LiveData<ImageOfTheDay?>
        get() = _imageOfTheDay

    private var _nasaApiStatus = MutableLiveData<NasaApiStatus>()
    val nasaApiStatus: LiveData<NasaApiStatus>
        get() = _nasaApiStatus

    init {
        _nasaApiStatus.value = NasaApiStatus.LOADING
        reloadAsteroids(AsteroidsFilter.SAVED)
        viewModelScope.launch {
            _imageOfTheDay.value = repository.getImageOfTheDay()
        }
    }

    fun reloadAsteroids(filter: AsteroidsFilter) {
        viewModelScope.launch {
            _nasaApiStatus.value = NasaApiStatus.LOADING
            try {
                repository.getAsteroids(filter)
                _nasaApiStatus.value = NasaApiStatus.DONE
            } catch (e: Exception) {
                _nasaApiStatus.value = NasaApiStatus.ERROR
            }
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