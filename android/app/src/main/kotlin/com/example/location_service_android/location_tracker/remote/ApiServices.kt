package com.example.location_service_android.location_tracker.remote

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT

interface ApiService {
    @PUT("bo/api/driver/execute")
    fun sendLocationInfo(@Body locationInfo: RequestBody): Call<ResponseBody>
}