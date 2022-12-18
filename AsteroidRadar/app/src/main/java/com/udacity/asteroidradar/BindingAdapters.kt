package com.udacity.asteroidradar

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.main.NasaApiStatus
import com.udacity.asteroidradar.network.ImageOfTheDay

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("asteroidStatusDescription")
fun bindDetailsStatusDescription(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.contentDescription = context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.contentDescription = context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgOfTheDay: ImageOfTheDay?) {
    imgOfTheDay?.let {
        if (imgOfTheDay.isImage()) {
            Picasso.Builder(imgView.context)
                .build()
                .load(imgOfTheDay.url)
                .placeholder(R.drawable.placeholder_picture_of_day)
                .into(imgView)
        }
    }
}

@BindingAdapter("imgDescription")
fun bindImageDescription(imgView: ImageView, description: String?) {
    val context = imgView.context
    if (description != null) {
        imgView.contentDescription = context.getString(R.string.nasa_picture_of_day_content_description_format, description)
    } else {
        imgView.contentDescription = context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
    }
}

@BindingAdapter("nasaApiStatus")
fun bindNasaStatus(progressBar: ProgressBar, status: NasaApiStatus) {
    when(status) {
        NasaApiStatus.LOADING -> {
            progressBar.visibility = View.VISIBLE
        }
        else -> {
            progressBar.visibility = View.GONE
        }
    }
}
