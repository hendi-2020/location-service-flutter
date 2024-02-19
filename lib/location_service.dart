import 'package:flutter/services.dart';

class LocationService {
  static const methodChannelStr = 'android_location_service/mc_location_service';
  static const eventChannelStr = 'android_location_service/ec_location_service';

  static const methodChannel = MethodChannel(methodChannelStr);
  static const eventChannel = EventChannel(eventChannelStr);

  startLocationTracking({
    required String token,
    int interval = 10000,
    int minDistance = 20,
    bool skipCallApi = true,
    bool enableLoop = false,
  }) {
    final data = {
      "dft": {
        "token": token,
        "device": "unknown",
        "lang": 2,
        "platform": "a",
        "ver": 10
      },
      "api": {"cls": "Sys", "mtd": "sendLocationInfo", "prm": null}
    };

    final args = {
      "data": data,
      "interval": interval,
      "minimum_distance": minDistance,
      "skip_call_api": skipCallApi,
      "enable_loop": enableLoop
    };

    methodChannel.invokeMethod('startLocationTracking', args);
  }

  stopLocationTracking() {
    methodChannel.invokeMethod('stopLocationTracking');
  }

  Stream<Location> receiveLocationStream() {
    return eventChannel
        .receiveBroadcastStream()
        .asyncMap((event) => Location(event["lat"], event["lon"]));
  }
}

class Location {
  String lat;
  String lon;

  Location(this.lat, this.lon);
}