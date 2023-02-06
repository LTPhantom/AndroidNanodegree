package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var loadingButton: LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }

        createChannel()

        loadingButton = findViewById(R.id.custom_button)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "File downloaded"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            loadingButton.stopAnimation()
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id != downloadID) {
                return;
            }
            val detailIntent = Intent(applicationContext, DetailActivity::class.java)

            val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val query = DownloadManager.Query()
            query.setFilterById(id)
            val cursor: Cursor = downloadManager.query(query)
            cursor.moveToNext()
            val status = getFileStatus(cursor, downloadManager);
            detailIntent.putExtra("STATUS", status)
            @SuppressLint("Range")
            val title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
            detailIntent.putExtra("TITLE", title)

            pendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                detailIntent,
                (PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            )

            action =
                NotificationCompat.Action(R.drawable.ic_assistant_black_24dp,
                    getString(R.string.check_status),
                    pendingIntent)

            notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager
            val builder = NotificationCompat.Builder(
                context,
                context.getString(R.string.notification_channel_id)
            )
                .setSmallIcon(R.drawable.ic_assistant_black_24dp)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_description))
                .setAutoCancel(true)
                .addAction(action)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            notificationManager.notify(0, builder.build())
        }
    }

    @SuppressLint("Range")
    private fun getFileStatus(cursor: Cursor, downloadManager: DownloadManager): String {
        val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
        if (status == DownloadManager.STATUS_SUCCESSFUL) {
            return getString(R.string.success)
        } else if (status == DownloadManager.STATUS_FAILED) {
            return getString(R.string.fail)
        } else if (status == DownloadManager.STATUS_PAUSED) {
            return getString(R.string.paused)
        } else if (status == DownloadManager.STATUS_PENDING) {
            return getString(R.string.pending)
        } else if (status == DownloadManager.STATUS_RUNNING) {
            return getString(R.string.running)
        } else {
            return getString(R.string.unknown_status)
        }
    }

    private fun download() {
        val requestURL = getSelectedURL()
        if (requestURL == null) {
            Toast.makeText(this, R.string.toast_message, Toast.LENGTH_LONG).show()
            return
        }
        val requestTitle = getRequestTitle()
        val request =
            DownloadManager.Request(Uri.parse(requestURL))
                .setTitle(requestTitle)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun getSelectedURL(): String? {
        return when (radio_group_options.checkedRadioButtonId) {
            R.id.glide_radio_button -> {
                URL_GLIDE
            }
            R.id.loadapp_radio_button -> {
                URL_LOADAPP
            }
            R.id.retrofit_radio_button -> {
                URL_RETROFIT
            }
            else -> {
                null
            }
        }
    }

    private fun getRequestTitle(): String {
        return when (radio_group_options.checkedRadioButtonId) {
            R.id.glide_radio_button -> {
                getString(R.string.glide_image_loading_library)
            }
            R.id.loadapp_radio_button -> {
                getString(R.string.loadapp_current_repository)
            }
            R.id.retrofit_radio_button -> {
                getString(R.string.retrofit_type_safe_http_client_for_android_and_java)
            }
            else -> {
                "No title"
            }
        }
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE = "https://github.com/bumptech/glide"
        private const val URL_LOADAPP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val URL_RETROFIT = "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }

}
