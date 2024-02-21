package com.olu.app.driver.location_tracker.remote

interface ILocationDataSender {
    fun sendCurrentLocation(lat: String, lon: String)
    fun dispose()

    interface LocationDataSenderListener {
        fun onRequestSuccess(lat: String, lon: String, response: String)
        fun onRequestFailed()
    }
}