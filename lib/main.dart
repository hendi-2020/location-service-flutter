import 'package:flutter/material.dart';
import 'package:location_service_android/location_service.dart';
import 'package:location_service_android/permission_manager.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Track Location Service'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final locationService = LocationService();

  final _listData = <String>[];
  final _listTime = <String>[];

  void _startLocationTrackingService() {
    PermissionManager.checkAndRequestTrackingLocationPermission().then((value) {
      const token =
          "GBczIkdeFn4TUBErXUYSVAAZAQAIBVkKBlgXbnpwEgcCAVZBHxExPRMDQwFLQ01AJShBAhNFCFoIWEEMQRsaNC8aC0EMUlAOAgAGD1VVUxAf";

      locationService.startLocationTracking(
        token: token, // user login token
        interval: 10000, // request location interval to fetch location update
        minDistance: 20, // minimum distance user should move to fetch location update
        skipCallApi: true, // this is for testing purpose, set `true` to ignore send location to server
        enableLoop: false, // request option, if `true` will loop every interval time, if `false` will send only get new position
      );
    });
  }

  void _stopLocationTrackingService() {
    locationService.stopLocationTracking();
    _clearListData();
  }

  _clearListData() {
    setState(() {
      _listData.clear();
      _listTime.clear();
    });
  }

  @override
  void initState() {
    super.initState();
    // start to listen location sent stream
    locationService.receiveLocationStream().listen((location) {
      _listData.add("${location.lon}, ${location.lat}");
      _listTime.add(_getCurrentTime());
      setState(() {});
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Column(
        children: [
          const SizedBox(height: 12),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              OutlinedButton(
                onPressed: _startLocationTrackingService,
                child: const Text("Start Service"),
              ),
              OutlinedButton(
                onPressed: _stopLocationTrackingService,
                child: const Text("Stop Service"),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Expanded(
              child: ListView.builder(
            itemCount: _listData.length,
            itemBuilder: (context, i) {
              return ListTile(
                leading: Text(_listData[i]),
                trailing: Text(_listTime[i]),
              );
            },
          )),
        ],
      ),
    );
  }

  String _getCurrentTime() {
    final now = DateTime.now();
    return "${now.hour.toString().padLeft(2, '0')}:${now.minute.toString().padLeft(2, '0')}:${now.second.toString().padLeft(2, '0')}";
  }
}
