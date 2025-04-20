import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  static const platform = MethodChannel('lane_detection_channel');
  Uint8List? laneImage;

  @override
  void initState() {
    super.initState();

    platform.setMethodCallHandler((call) async {
      if (call.method == "onFrame") {
        final bytes = call.arguments as Uint8List;
        setState(() {
          laneImage = bytes;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: Text("Lane Detection2")),
        body: Center(
          child: laneImage != null
              ? Image.memory(laneImage!)
              : Text("Kamera başlatılıyor..."),
        ),
      ),
    );
  }
}
