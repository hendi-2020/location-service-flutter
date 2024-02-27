package com.olu.app.driver.location_tracker.remote

import com.google.gson.JsonObject

data class ResponseEntity(
    val ret: Int? = null,
    val msg: String? = null,
    val result: JsonObject? = null
)