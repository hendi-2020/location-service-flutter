package com.example.location_service_android.location_tracker.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.location_service_android.R

class NotificationHelper(private val context: Context) {
    companion object {
        const val NOTIFICATION_ID = 1

        private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
        private const val NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager)?.run {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
                createNotificationChannel(channel)
            }
        }
    }

    fun getNotification(): NotificationCompat.Builder {
        createNotificationChannel()
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Location Tracker")
            .setContentText("Location tracking initializing....")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
    }

    fun updateNotification(lat: String, lon: String) {
        val notification = getNotification()
        val nm = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        val updatedNotification = notification.setContentText("Location: $lon, $lat")
        nm.notify(NOTIFICATION_ID, updatedNotification.build())
    }
}