package com.olu.app.driver.location_tracker.client

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationServices

class LocationClient(
    private val context: Context,
    private val callback: (Location) -> Unit
) : ILocationClient {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            result.locations.lastOrNull()?.let { location ->
                callback.invoke(location)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long, minimumDistance: Float) {
        if (!context.hasLocationPermission()) {
            throw ILocationClient.LocationException("Location permission not granted")
        }

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            throw ILocationClient.LocationException("Location service disabled, please turn GPS on")
        }

        val request =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval).apply {
                setMinUpdateDistanceMeters(minimumDistance)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()

        client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    override fun dispose() {
        client.removeLocationUpdates(locationCallback)
    }

    private fun Context.hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}