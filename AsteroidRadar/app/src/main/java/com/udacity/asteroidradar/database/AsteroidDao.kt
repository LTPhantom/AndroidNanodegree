package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.domain.Asteroid

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroidentity WHERE date(closeapproachdate) BETWEEN :startDate AND :endDate ORDER BY closeapproachdate ASC")
    suspend fun getAsteroids(startDate: String, endDate: String): List<AsteroidEntity>

    @Query("SELECT * FROM asteroidentity ORDER BY closeapproachdate ASC")
    suspend fun getSavedAsteroids(): List<AsteroidEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsteroids(vararg asteroids: AsteroidEntity)

    @Query("DELETE FROM asteroidentity WHERE date(closeapproachdate) < date(:beforeToday)")
    suspend fun deleteAsteroids(beforeToday: String)
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