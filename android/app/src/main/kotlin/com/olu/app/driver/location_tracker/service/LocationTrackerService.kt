package com.olu.app.driver.location_tracker.service

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.olu.app.driver.ANDROID_LOCATION_SERVICE_SENT_BROADCAST
import com.olu.app.driver.REQUEST_LOCATION_INTERVAL
import com.olu.app.driver.REQUEST_LOCATION_MINIMUM_DISTANCE
import com.olu.app.driver.location_tracker.client.ILocationClient
import com.olu.app.driver.location_tracker.client.LocationClient
import com.olu.app.driver.location_tracker.remote.ILocationDataSender
import com.olu.app.driver.location_tracker.remote.LocationDataSender
import com.olu.app.driver.location_tracker.remote.LocationDataSenderWithLoopInterval
import com.olu.app.driver.location_tracker.remote.LocationRequester

class LocationTrackerService : Service(), ILocationDataSender.LocationDataSenderListener {

    private var mLocationClient: ILocationClient? = null
    private var mLocationDataSender: ILocationDataSender? = null
    private var mNotificationHelper: NotificationHelper? = null

    private var mInterval = REQUEST_LOCATION_INTERVAL
    private var mMinimumDistance = REQUEST_LOCATION_MINIMUM_DISTANCE
    private var mData = "{}"
    private var mSkipCallApi = false
    private var mEnableLoop = false

    private var isTrackingStated = false

    companion object {
        const val ACTION_START_TRACKING = "ACTION_START_TRACKING"
        const val ACTION_STOP_TRACKING = "ACTION_STOP_TRACKING"

        const val EXTRA_KEY_INTERVAL = "extra_interval"
        const val EXTRA_KEY_MINIMUM_DISTANCE = "extra_minimum_distance"
        const val EXTRA_KEY_DATA = "extra_data"
        const val EXTRA_KEY_SKIP_CALL_API = "extra_skip_call_api"
        const val EXTRA_KEY_ENABLE_LOOP = "extra_enable_loop"

        const val EXTRA_KEY_ARGS = "extra_args"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mNotificationHelper = NotificationHelper(applicationContext)
        mLocationClient = LocationClient(applicationContext) {
            val lat = it.latitude.toString()
            val lon = it.longitude.toString()

            // update notification
            mNotificationHelper?.updateNotification(lat, lon)

            // send location to server
            mLocationDataSender?.sendCurrentLocation(lat, lon)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TRACKING -> {
                if (!isTrackingStated) {
                    isTrackingStated = true
                    intent.extras?.getBundle(EXTRA_KEY_ARGS)?.let {

                        // The params that passed from dart class
                        mInterval = it.getLong(EXTRA_KEY_INTERVAL, REQUEST_LOCATION_INTERVAL)
                        mMinimumDistance = it.getFloat(
                            EXTRA_KEY_MINIMUM_DISTANCE,
                            REQUEST_LOCATION_MINIMUM_DISTANCE
                        )
                        mData = it.getString(EXTRA_KEY_DATA, "{}")
                        mSkipCallApi = it.getBoolean(EXTRA_KEY_SKIP_CALL_API, false)
                        mEnableLoop = it.getBoolean(EXTRA_KEY_ENABLE_LOOP, false)

                        if (mEnableLoop) {
                            mLocationDataSender = LocationDataSenderWithLoopInterval(
                                LocationRequester(mData),
                                mInterval,
                                mSkipCallApi,
                                this
                            )
                        } else {
                            mLocationDataSender = LocationDataSender(
                                LocationRequester(mData),
                                mSkipCallApi,
                                this
                            )
                        }

                        startTracking()
                    }
                }
            }

            ACTION_STOP_TRACKING -> stopTracking()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onRequestSuccess(lat: String, lon: String) {
        showToast("Location sent")
        sendLocationBroadcast(lat, lon)
    }

    override fun onRequestFailed(message: String) {
        TODO("Not yet implemented")
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private fun startTracking() {
        try {
            mLocationClient?.getLocationUpdates(mInterval, mMinimumDistance)
            startForeground(
                NotificationHelper.NOTIFICATION_ID,
                mNotificationHelper?.getNotification()?.build()
            )
        } catch (e: ILocationClient.LocationException) {
            stopTracking()
            showToast(e.error)
        }
    }

    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendLocationBroadcast(lat: String, lon: String) {
        sendBroadcast(Intent(ANDROID_LOCATION_SERVICE_SENT_BROADCAST).apply {
            putExtra("lat", lat)
            putExtra("lon", lon)
        })
    }

    @SuppressLint("NewApi")
    private fun stopTracking() {
        mLocationClient?.dispose()
        mLocationDataSender?.dispose()

        isTrackingStated = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            stopForeground(true)
        }
        stopSelf()
    }
}