package com.olu.app.driver.location_tracker.client

import android.location.Location

interface ILocationClient {
    fun getLocationUpdates(interval: Long, minimumDistance: Float)
    fun getLastLocation(callback: (lon: Double, lat: Double) -> Unit)
    fun dispose()
    class LocationException(val error: String) : Exception()
}