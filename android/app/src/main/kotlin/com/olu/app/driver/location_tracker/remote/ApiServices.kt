package com.olu.app.driver.location_tracker.remote

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

interface ApiService {

    @PUT
    fun sendLocationInfo(@Url fullUrl: String, @Body locationInfo: RequestBody): Call<ResponseEntity>
}