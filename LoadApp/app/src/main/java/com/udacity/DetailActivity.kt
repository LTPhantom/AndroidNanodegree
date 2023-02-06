package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val titleExtra = intent.getStringExtra("TITLE")
        val statusExtra = intent.getStringExtra("STATUS")
        val fileName: TextView = findViewById(R.id.file_name)
        val status: TextView = findViewById(R.id.status)
        fileName.text = titleExtra
        status.text = statusExtra

        val okButton: Button = findViewById(R.id.ok_button)
        okButton.setOnClickListener {
            finish()
        }

        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelAll()
    }
}
