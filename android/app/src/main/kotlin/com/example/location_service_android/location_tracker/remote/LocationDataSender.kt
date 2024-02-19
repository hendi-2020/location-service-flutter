package com.example.location_service_android.location_tracker.remote

class LocationDataSender(
    private val locationRequester: LocationRequester,
    private val skippCallApi: Boolean,
    private val listener: ILocationDataSender.LocationDataSenderListener?
) : ILocationDataSender {

    override fun sendCurrentLocation(lat: String, lon: String) {
        if (skippCallApi) {
            listener?.onRequestSuccess(lat, lon)
        } else {
            when (val result = locationRequester.sendLocation(lat, lon)) {
                is LocationRequester.Result.Success -> listener?.onRequestSuccess(
                    lat,
                    lon
                )

                is LocationRequester.Result.Failed -> listener?.onRequestFailed(result.message)
            }
        }
    }

    override fun dispose() = Unit
}