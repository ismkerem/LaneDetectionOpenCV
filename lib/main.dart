import 'dart:typed_data';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:camera/camera.dart';

late List<CameraDescription> cameras;

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  cameras = await availableCameras();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: LaneDetectionScreen(),
    );
  }
}


class LaneDetectionScreen extends StatefulWidget {
  @override
  _LaneDetectionScreenState createState() => _LaneDetectionScreenState();
}

class _LaneDetectionScreenState extends State<LaneDetectionScreen> {
  final platform = const MethodChannel('lane_detection_channel');
  CameraController? controller;
  Uint8List? processedImage;
  bool isProcessing = false;

  @override
  void initState() {
    super.initState();
    initCamera();
  }

  Future<void> initCamera() async {
    try {
      // İlk kamerayı başlatıyoruz
      controller = CameraController(cameras[0], ResolutionPreset.medium);
      await controller!.initialize();

      controller!.startImageStream((CameraImage image) async {
        // Eğer işlem yapılıyorsa veya YUV420 formatı değilse, işlem yapma
        if (isProcessing || image.format.group != ImageFormatGroup.yuv420) return;

        isProcessing = true;

        try {
          // YUV420 formatını düz bir şekilde alıyoruz
          final bytes = image.planes[0].bytes;

          // Native kodu çağırıyoruz
          final result = await platform.invokeMethod<Uint8List>('detectLanes', {
            'data': bytes,
            'width': image.width,
            'height': image.height,
          });

          if (mounted && result != null) {
            setState(() {
              processedImage = result; // İşlenmiş resmi güncelliyoruz
            });
          }
        } catch (e) {
          print("Error invoking native code: $e");
        } finally {
          isProcessing = false;
        }
      });

      setState(() {}); // UI'ı yeniden oluştur
    } catch (e) {
      print("Camera initialization error: $e");
    }
  }

  @override
  Widget build(BuildContext context) {
    if (controller == null || !controller!.value.isInitialized) {
      return Scaffold(
        appBar: AppBar(title: const Text('Lane Detection')),
        body: const Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Lane Detection')),
      body: Column(
        children: [
          Expanded(
            child: processedImage == null
                ? Center(child: CameraPreview(controller!)) // Kameradan gelen görüntüyü göster
                : Image.memory(
              processedImage!, // İşlenmiş resmi ekranda göster
              fit: BoxFit.contain,
            ),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    controller?.dispose();
    super.dispose();
  }
}
