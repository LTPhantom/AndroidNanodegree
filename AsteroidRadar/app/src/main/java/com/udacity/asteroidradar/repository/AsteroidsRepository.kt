package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainObjects
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.asEntityObjects
import com.udacity.asteroidradar.network.ImageOfTheDay
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

private const val API_KEY = "DEMO_KEY"

enum class AsteroidsFilter { WEEK, TODAY, SAVED }
class AsteroidsRepository(private val database: AsteroidDatabase) {

    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>?>
        get() = _asteroids

    suspend fun getImageOfTheDay(): ImageOfTheDay? {
        return try {
            Network.nasa.getImageOfTheDayMetadata(API_KEY)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAsteroids(filter: AsteroidsFilter) {

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        var startDate = dateFormat.format(calendar.time)

        if (filter == AsteroidsFilter.SAVED) {
            _asteroids.value = database.asteroidDao.getSavedAsteroids(startDate).asDomainObjects()
            return
        }

        if (filter == AsteroidsFilter.WEEK) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            startDate = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        val endDate = dateFormat.format(calendar.time)
        try {
            val asteroidsJson = Network.nasa.getAsteroidList(startDate, endDate, API_KEY)
            val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsJson.string()))
            database.asteroidDao.insertAsteroids(*asteroids.asEntityObjects())
        } catch (e: Exception) {
            _asteroids.value =
                database.asteroidDao.getAsteroids(startDate, endDate).asDomainObjects()
            throw Exception(e)  // Error is handled on ViewModel
        }
        _asteroids.value =
            database.asteroidDao.getAsteroids(startDate, endDate).asDomainObjects()
    }

    suspend fun deleteAsteroidsBeforeToday() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val today = dateFormat.format(calendar.time)
        database.asteroidDao.deleteAsteroids(today)
    }
}