package com.example.location_service_android.location_tracker.client

interface ILocationClient {
    fun getLocationUpdates(interval: Long, minimumDistance: Float)
    fun dispose()
    class LocationException(val error: String) : Exception()
}