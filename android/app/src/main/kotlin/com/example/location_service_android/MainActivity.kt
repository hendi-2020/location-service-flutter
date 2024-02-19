package com.example.location_service_android

import androidx.annotation.NonNull
import com.example.location_service_android.location_tracker.LocationActivityManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

class MainActivity : FlutterActivity() {
    private val locationActivityManager = LocationActivityManager(this)

    override fun onStop() {
        locationActivityManager.unregisterBroadcast()
        super.onStop()
    }

    override fun onStart() {
        locationActivityManager.registerBroadcast()
        super.onStart()
    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        locationActivityManager.configureFlutterEngine(flutterEngine)
    }
}
