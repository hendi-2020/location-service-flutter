package com.olu.app.driver.location_tracker.client

interface ILocationClient {
    fun getLocationUpdates(interval: Long, minimumDistance: Float)
    fun dispose()
    class LocationException(val error: String) : Exception()
}