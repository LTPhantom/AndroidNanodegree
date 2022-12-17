package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainObjects
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.asEntityObjects
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

private const val API_KEY = "bOtDblDYHuRS3ATgh2D7HRQ47egQ26nlXzrFJtEf"

class AsteroidsRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainObjects()
    }

    suspend fun getAsteroids() {
        try {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
            val startDate = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, 7)
            val endDate = dateFormat.format(calendar.time)

            val asteroidsJson = Network.nasa.getAsteroidList(startDate, endDate, API_KEY)
            val asteroids = parseAsteroidsJsonResult(JSONObject(asteroidsJson.string()))
            database.asteroidDao.deleteAsteroids()  //TODO: Delete this line when autodownload is implemented
            database.asteroidDao.insertAsteroids(*asteroids.asEntityObjects())
        }catch (e: Exception) {
            // TODO: Change to proper Exception handling on Repository
            Log.d("AsteroidsRepository", "Could not get asteroids. ${e.message}")
        }
    }
}