package com.udacity.asteroidradar.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageOfTheDay(
    val url: String,
    @Json(name = "media_type") val mediaType: String,
    val title: String,
) : Parcelable {
    fun isImage(): Boolean {
        return mediaType == "image"
    }
}