import 'package:permission_handler/permission_handler.dart';

class PermissionManager {
  static Future<bool> checkAndRequestTrackingLocationPermission() async {
    final notification = await _checkAndRequestNotificationPermission();
    if (notification) {
      return _checkAndRequestLocationPermission();
    }
    return Future(() => true);
  }

  static Future<bool> _checkAndRequestNotificationPermission() async {
    final isGranted = await Permission.notification.isGranted;

    if (isGranted) {
      return Future(() => true);
    }

    final status = await Permission.notification.request();
    return Future(() => status == PermissionStatus.granted);
  }

  static Future<bool> _checkAndRequestLocationPermission() async {
    final isGranted = await Permission.location.isGranted;

    if (isGranted) {
      return Future(() => true);
    }

    final status = await Permission.location.request();
    return Future(() => status == PermissionStatus.granted);
  }
}
