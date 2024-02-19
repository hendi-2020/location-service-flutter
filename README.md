
# Android Location Tracker Configuration

## Native Android Settings

These some configurations should be added on native code (android module) side.

### 1. `AndroidManifest.xml`

Put the permissions which are related to location permissions.

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

Since we are using non secure http instead of https we should add this property inside `<application>` tag. Http request will not working on release build without this config.
```xml
<application android:usesCleartextTraffic="true"></application>
``` 

Bind service class inside `<application></application>`.

```xml
<service
    android:name=".location_tracker.service.LocationTrackerService"
    android:foregroundServiceType="location" />
```

&nbsp;
### 2. Add all dependency in `app/build.gradle`. 
```groovy
dependencies {
    implementation 'com.google.android.gms:play-services-location:21.0.1'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
}
```

&nbsp;
### 3. Copy all required classes.
Copy all classes in `location_tracker` folder and paste inside root folder of android package.

![A test image](/screenshot/all_classes.png)

&nbsp;
### 4. `MainActivity.kt`.
```kotlin
class MainActivity : FlutterActivity() {
    private val locationActivityManager = LocationActivityManager(this)

    override fun onStop() {
        locationActivityManager.unregisterBroadcast()
        super.onStop()
    }

    override fun onStart() {
        locationActivityManager.registerBroadcast()
        super.onStart()
    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        locationActivityManager.configureFlutterEngine(flutterEngine)
    }
}
```

&nbsp;
### 5. [OPTIONAL] Change API base url on `RetrofitHelper.kt`.
```kotlin
private const val BASE_URL = "http://34.101.74.181:8080/" // base url
private const val TIME_OUT = 30L // defined request time out
```

&nbsp;
## Flutter Settings
### Implement `location_service.dart` to manage service.
Create new instance of `LocationService` on stateful widget.
```dart
final locationService = LocationService();
```
This is the function to start tracking service.
```dart
locationService.startLocationTracking(
    token: token, // user login token
    interval: 10000, // request location interval to fetch location update
    minDistance: 20, // minimum distance user should move to fetch location update
    skipCallApi: true, // this is for testing purpose, set `true` to ignore send location to server
    enableLoop: false, // request option, if `true` will loop every interval time, if `false` will send only get new position 
);
```
This is the function to stop tracking service.
```dart
locationService.stopLocationTracking();
```
Add stream listener to get location update from service on flutter page.
```dart
@override
void initState() {
  super.initState();
  locationService.receiveLocationStream().listen((location) {
      // Do something with the data.
  });
}
```
