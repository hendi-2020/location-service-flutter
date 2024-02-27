package com.olu.app.driver.location_tracker.remote

class LocationDataSender(
    private val locationRequester: LocationRequester,
    private val skippCallApi: Boolean,
    private val listener: ILocationDataSender.LocationDataSenderListener?
) : ILocationDataSender {

    override fun sendCurrentLocation(lat: String, lon: String) {
        if (skippCallApi) {
            listener?.onRequestSuccess(lat, lon, ResponseEntity())
        } else {
            locationRequester.sendLocation(lat, lon) { result ->
                when (result) {
                    is LocationRequester.Result.Success -> listener?.onRequestSuccess(
                        lat,
                        lon,
                        result.responseEntity ?: ResponseEntity()
                    )

                    is LocationRequester.Result.Failed -> listener?.onRequestFailed()
                }
            }
        }
    }

    override fun dispose() = Unit
}