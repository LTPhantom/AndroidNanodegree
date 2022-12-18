package com.udacity.asteroidradar

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsFilter
import com.udacity.asteroidradar.repository.AsteroidsRepository

class AsteroidRadarWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORKER_NAME = "AsteroidRadarWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidsRepository(database)

        return try {
            repository.getAsteroids(AsteroidsFilter.WEEK)
            repository.deleteAsteroidsBeforeToday()
            Result.success()
        }catch (e: Exception){
            Result.retry()
        }
    }
}