package com.olu.app.driver.location_tracker.remote

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationRequester(data: String, baseUrl: String) {

    companion object {
        private const val PATH = "bo/api/driver/execute"
    }

    private var jsonBody = JSONObject()
    private var fullUrl = when {
        baseUrl.last() == '/' -> baseUrl.plus(PATH)
        else -> baseUrl.plus("/").plus(PATH)
    }

    init {
        try {
            jsonBody = JSONObject(data)
        } catch (_: Exception) {
        }
    }

    fun sendLocation(lat: String, lon: String, callback: (Result) -> Unit) {
        val apiService = RetrofitHelper.getService()
        val body = generateRequestBody(lat, lon)

        apiService.sendLocationInfo(fullUrl, body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                callback.invoke(Result.Success(response.body()?.string().orEmpty()))
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback.invoke(Result.Failed)
            }
        })
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
        class Success(val response: String) : Result()
        object Failed : Result()
    }
}