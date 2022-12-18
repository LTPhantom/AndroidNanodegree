package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.AsteroidEntity
import com.udacity.asteroidradar.database.asDomainObjects
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.asEntityObjects
import com.udacity.asteroidradar.network.ImageOfTheDay
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

private const val API_KEY = "bOtDblDYHuRS3ATgh2D7HRQ47egQ26nlXzrFJtEf"

enum class AsteroidsFilter { WEEK, TODAY, SAVED }
class AsteroidsRepository(private val database: AsteroidDatabase) {

    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>?>
        get() = _asteroids

    suspend fun getImageOfTheDay(): ImageOfTheDay? {
        return try {
            Network.nasa.getImageOfTheDayMetadata(API_KEY)
        } catch (e: Exception) {
            // TODO: Change to proper Exception handling on Repository
            Log.d("AsteroidsRepository", "Could not download Image Of The Day. ${e.message}")
            null
        }
    }

    suspend fun getAsteroids(filter: AsteroidsFilter) {
        if (filter == AsteroidsFilter.SAVED) {
            _asteroids.value = database.asteroidDao.getSavedAsteroids().asDomainObjects()
            return
        }

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val startDate = dateFormat.format(calendar.time)
        if (filter == AsteroidsFilter.WEEK) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        val endDate = dateFormat.format(calendar.time)
        try {
            val asteroidsJson = Network.nasa.getAsteroidList(startDate, endDate, API_KEY)
            val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsJson.string()))
            database.asteroidDao.insertAsteroids(*asteroids.asEntityObjects())
            _asteroids.value =
                database.asteroidDao.getAsteroids(startDate, endDate).asDomainObjects()
        } catch (e: Exception) {
            throw Exception(e)  // Error is handled on ViewModel
        }
    }
}