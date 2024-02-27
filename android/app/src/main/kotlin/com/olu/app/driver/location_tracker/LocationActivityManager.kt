package com.olu.app.driver.location_tracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.annotation.NonNull
import com.olu.app.driver.location_tracker.service.LocationTrackerService
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel

class LocationActivityManager(private val activity: FlutterActivity) {

    private var sinkLocationBR: SinkBroadCastReceiver? = null

    fun registerBroadcast() {
        sinkLocationBR?.let {
            activity.registerReceiver(it, IntentFilter(ANDROID_LOCATION_SERVICE_SENT_BROADCAST))
        }
    }

    fun unregisterBroadcast() {
        try {
            sinkLocationBR?.let { activity.unregisterReceiver(it) }
        } catch (_: Exception) {
        }
    }

    fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            ANDROID_LOCATION_SERVICE_METHOD_CHANNEL
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                "startLocationTracking" -> {
                    val args = call.arguments as Map<String, Any>
                    startLocationTracking(args)
                }

                "stopLocationTracking" -> {
                    stopLocationTracking()
                }

                else -> {
                    result.notImplemented()
                }
            }
        }

        EventChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            ANDROID_LOCATION_SERVICE_EVENT_CHANNEL
        ).setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                events?.let {
                    sinkLocationBR = SinkBroadCastReceiver(it)
                    activity.registerReceiver(sinkLocationBR, IntentFilter(
                        ANDROID_LOCATION_SERVICE_SENT_BROADCAST
                    ))
                }
            }

            override fun onCancel(arguments: Any?) = Unit
        })
    }

    /**
     * args: The parameters that passed from dart class.
     * */
    private fun mapArgsToBundle(args: Map<String, Any>): Bundle {
        val baseUrl = args["base_url"] as String
        val data = args["data"] as Map<String, Any>
        val interval = args["interval"] as Int
        val minimumDistance = args["minimum_distance"] as Int
        val skipCallApi = args["skip_call_api"] as Boolean
        val enableLoop = args["enable_loop"] as Boolean
        val errorTokenCode = args["error_token_code"] as Int

        return Bundle().apply {
            putString(LocationTrackerService.EXTRA_KEY_BASE_URL, baseUrl)
            putString(LocationTrackerService.EXTRA_KEY_DATA, data.toString())
            putLong(LocationTrackerService.EXTRA_KEY_INTERVAL, interval.toLong())
            putFloat(LocationTrackerService.EXTRA_KEY_MINIMUM_DISTANCE, minimumDistance.toFloat())
            putBoolean(LocationTrackerService.EXTRA_KEY_SKIP_CALL_API, skipCallApi)
            putBoolean(LocationTrackerService.EXTRA_KEY_ENABLE_LOOP, enableLoop)
            putInt(LocationTrackerService.EXTRA_KEY_ERROR_TOKEN_CODE, errorTokenCode)
        }
    }

    private fun startLocationTracking(args: Map<String, Any>) {
        Intent(activity.applicationContext, LocationTrackerService::class.java).apply {
            action = LocationTrackerService.ACTION_START_TRACKING
            putExtra(LocationTrackerService.EXTRA_KEY_ARGS, mapArgsToBundle(args))
            activity.startService(this)
        }
    }

    private fun stopLocationTracking() {
        Intent(activity.applicationContext, LocationTrackerService::class.java).apply {
            action = LocationTrackerService.ACTION_STOP_TRACKING
            activity.startService(this)
        }
    }

    inner class SinkBroadCastReceiver(private val sink: EventChannel.EventSink) :
        BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.extras?.let {
                val lon = it.getString("lon")
                val lat = it.getString("lat")
                val response = it.getString("response")
                val location = mapOf("lon" to lon, "lat" to lat, "response" to response)
                sink.success(location)
            }
        }
    }
}