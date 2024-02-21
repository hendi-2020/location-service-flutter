package com.olu.app.driver.location_tracker

const val REQUEST_LOCATION_INTERVAL = 10000L
const val REQUEST_LOCATION_MINIMUM_DISTANCE = 20.0f

const val ANDROID_LOCATION_SERVICE_METHOD_CHANNEL = "android_location_service/mc_location_service"
const val ANDROID_LOCATION_SERVICE_EVENT_CHANNEL = "android_location_service/ec_location_service"
const val ANDROID_LOCATION_SERVICE_SENT_BROADCAST = "android_location_service_android/bc_location_service"

const val DEFAULT_BASE_URL = "http://34.101.74.181:8080/"