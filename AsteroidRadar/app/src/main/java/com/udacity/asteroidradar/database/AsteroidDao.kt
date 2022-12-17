package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.domain.Asteroid

@Dao
interface AsteroidDao {
    //TODO: Get asteroids from Today only
    @Query("SELECT * FROM asteroidentity ORDER BY closeapproachdate ASC")
    fun getAsteroids(): LiveData<List<AsteroidEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsteroids(vararg asteroids: AsteroidEntity)

    // TODO: Add a proper Delete of old data and not everything
    @Query("DELETE FROM asteroidentity")
    suspend fun deleteAsteroids()
}

fun List<AsteroidEntity>.asDomainObjects(): List<Asteroid> {
    return map {
        Asteroid(
            it.id,
            it.codename,
            it.closeApproachDate,
            it.absoluteMagnitude,
            it.estimatedDiameter,
            it.relativeVelocity,
            it.distanceFromEarth,
            it.isPotentiallyHazardous,
        )
    }
}