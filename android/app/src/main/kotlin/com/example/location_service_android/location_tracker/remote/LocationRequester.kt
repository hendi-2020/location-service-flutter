package com.example.location_service_android.location_tracker.remote

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class LocationRequester(data: String) {

    private var jsonBody = JSONObject()

    init {
        try {
            jsonBody = JSONObject(data)
        } catch (_: Exception) {
        }
    }

    fun sendLocation(lat: String, lon: String): Result {
        val apiService = RetrofitHelper.getService()
        val body = generateRequestBody(lat, lon)
        val response = apiService.sendLocationInfo(body).execute()

        if (response.isSuccessful) {
            return Result.Success
        } else {
            return Result.Failed(response.message())
        }
    }

    private fun generateRequestBody(lat: String, lon: String): RequestBody {
        try {
            val api = jsonBody.getJSONObject("api").apply {
                val latLonObj = JSONObject().apply {
                    put("lat", lat)
                    put("lng", lon)
                }
                val lonLat = JSONArray().apply {
                    put(latLonObj)
                }
                put("prm", lonLat)
            }
            jsonBody.put("api", api)
        } catch (_: JSONException) {
        }

        return jsonBody
            .toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    sealed class Result {
        object Success : Result()
        class Failed(val message: String) : Result()
    }
}