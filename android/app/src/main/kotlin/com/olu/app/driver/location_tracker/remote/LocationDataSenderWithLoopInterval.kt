package com.olu.app.driver.location_tracker.remote

import java.util.Timer
import java.util.TimerTask

class LocationDataSenderWithLoopInterval(
    private val locationRequester: LocationRequester,
    private val interval: Long,
    private val skippCallApi: Boolean,
    private val listener: ILocationDataSender.LocationDataSenderListener?
) : ILocationDataSender {
    private var isRequesting = false
    private var mLat = ""
    private var mLon = ""
    private var timer: Timer? = null

    override fun sendCurrentLocation(lat: String, lon: String) {
        mLat = lat
        mLon = lon

        if (isRequesting) return

        if (timer == null) {
            timer = Timer()
            timer?.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    if (skippCallApi) {
                        listener?.onRequestSuccess(lat, lon, ResponseEntity())
                    } else {
                        isRequesting = true
                        locationRequester.sendLocation(lat, lon) { result ->
                            when (result) {
                                is LocationRequester.Result.Success -> listener?.onRequestSuccess(
                                    lat,
                                    lon,
                                    result.responseEntity ?: ResponseEntity()
                                )

                                is LocationRequester.Result.Failed -> listener?.onRequestFailed()
                            }
                            isRequesting = false
                        }
                    }
                }
            }, 0L, interval)
        }
    }

    override fun dispose() {
        timer?.cancel()
        timer = null
    }
}