package com.example.location_service_android.location_tracker.remote

interface ILocationDataSender {
    fun sendCurrentLocation(lat: String, lon: String)
    fun dispose()

    interface LocationDataSenderListener {
        fun onRequestSuccess(lat: String, lon: String)
        fun onRequestFailed(message: String)
    }
}