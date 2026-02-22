# MobileVeinViewer

A low-cost, non-invasive vein viewing system on Android smartphones that helps healthcare workers identify, locate, and examine superficial veins by providing an accurate augmented reality (AR) image in real time.

> [!IMPORTANT]
> **This application requires a custom hardware extension to function.** The proposed solution consists of two inseparable components: a low-cost hardware extension attached to a smartphone, and this Android application that receives and processes the data captured by sensors placed on that hardware extension. Without the hardware extension — a circuit of near-infrared LEDs and a NIR-pass-filtered webcam connected via a USB OTG cable — the application has no input to work with. Instructions for building the hardware extension are detailed in the [thesis](https://github.com/bakribitar/MobileVeinViewer-Thesis).

> **Master's Thesis** — *Vein Viewing System Using Hardware Extension Attached To Smartphones*  
> Technische Universität München, Department of Informatics  
> Author: Bakri Bitar &nbsp;|&nbsp; Supervisor: Prof. Dr. Uwe Baumgarten  
> 📄 **[Read the full thesis](https://github.com/bakribitar/MobileVeinViewer-Thesis)**

---

## Motivation

Venepuncture and intravenous cannulation are routinely performed in healthcare, yet success rates can drop below 50 % in difficult populations (paediatric, geriatric, and chronically ill patients). Failed attempts increase the risk of bruising, nerve damage, accidental arterial puncture, and patient distress. Dedicated commercial vein viewers deliver excellent accuracy but rely on advanced, expensive hardware — making them unaffordable for many clinics and countries.

**MobileVeinViewer** offers an affordable alternative (hardware cost under €30) by combining a simple NIR hardware extension with real-time image processing on an ordinary Android smartphone.

---

## How It Works

### Hardware Extension

| Component | Details |
|-----------|---------|
| **NIR LEDs** | 8 × SD-AR512C9 LEDs (940 nm, 60° radiation angle, 40 mA each) arranged in a grid |
| **NIR Camera** | Modified webcam (IR-Cut filter replaced with IR-Pass filter) to capture only near-infrared light |
| **Connection** | USB On-The-Go (OTG) cable — single cable for both data and power (< 500 mA total) |
| **Brightness Control** | Stepless variable resistor to dim or turn off the LEDs |

The 940 nm NIR light penetrates the epidermis and is absorbed by oxygenated haemoglobin in red blood cells flowing through veins, while surrounding tissues reflect/scatter the light — creating a contrast pattern the camera can capture.

### Software Application

The Android app receives the NIR video stream via a UVC (USB Video Class) camera library and processes each frame in real time using OpenCV through a C++ JNI layer.

**Two operation modes:**

| Mode | Description |
|------|-------------|
| **Raw NIR** | Unprocessed NIR images — good contrast for light-skinned patients, no computational overhead |
| **Adaptive Thresholding** | Local (mean or Gaussian) thresholding with configurable block size and correction constant; optional median blur for noise reduction — significantly improves vein contrast across different skin types |

Other algorithms were evaluated (Hough Transform, Laplacian edge detection, Difference of Gaussians) but adaptive thresholding provided the best results for vein visualization.

**Safety feature:** An accelerometer-based alarm alerts the user if the device is flipped upside down, preventing direct NIR exposure to eyes.

---

## Architecture

```
USB Camera (UVC) ──► libuvccamera (JNI / libuvc) ──► UVCCameraHandlerMultiSurface
                                                            │
                                            ┌───────────────┴───────────────┐
                                            ▼                               ▼
                                     UVCCameraTextureView           ImageProcessor
                                      (live preview)          (OpenGL → JNI / OpenCV C++)
                                                                        │
                                                                        ▼
                                                                SurfaceView (result)
                                                          (vein-enhanced image overlay)
```

---

## Project Structure

```
app/                          # Main Android application
├── MainActivity              # Camera preview, UI controls, accelerometer alarm
├── ImageProcessor (opencv/)  # OpenGL frame capture → native OpenCV processing
├── libuvccamera-release/     # UVC camera library (C++ / libuvc via JNI)
├── usbCameraCommon-release/  # Camera handler, media encoder, custom views
└── opencv/                   # OpenCV 3 SDK (Java bindings + native libs)
```

### Key Modules

| Module | Description |
|--------|-------------|
| `app` | Main activity, UI, permission handling, alarm system |
| `opencv` | `ImageProcessor.java` + native C++ image processing (adaptive threshold, Hough, Laplace, DoG) |
| `libuvccamera-release` | UVC camera access on non-rooted Android (libuvc + libjpeg-turbo) |
| `usbCameraCommon-release` | Camera handler abstraction, media encoding (MediaCodec/MediaMuxer), custom preview views |

---

## Image Processing Modes

| Constant | Mode | Description |
|----------|------|-------------|
| `NO_PROCESS` | Raw NIR | Direct camera output, no processing |
| `ADAPTIVE_THRESHOLD` | Adaptive Thresholding | Local thresholding for vein segmentation |
| `ADAPTIVE_THRESHOLD_MEDIAN_BLUR` | Adaptive Thresholding + Median Blur | Adds noise reduction via median filter |
| `HOUGH` | Hough Transform | Edge/line detection (experimental) |
| `LAPLACE` | Laplacian | Edge detection via second derivative (experimental) |
| `DoG` | Difference of Gaussians | Band-pass filter (experimental) |

---

## Results

The system was tested on subjects with varying skin conditions:

- **Normal skin** — Good vein contrast in both raw NIR and adaptive thresholding modes
- **Tattooed skin** — NIR light penetrates most tattoo pigments; dye density and dark ink colours can reduce visibility
- **Different skin colours** — Adaptive thresholding significantly improves contrast on darker skin tones where raw NIR may be insufficient
- **Skin diseases** — Tested on various conditions with promising results

---

## Requirements

- Android device with USB OTG support and UVC camera compatibility (see [compatible devices](https://github.com/bakribitar/MobileVeinViewer-Thesis) list in thesis appendix)
- Android API 18+ (Android 4.3 Jelly Bean or higher)
- OpenGL ES 3.0 support
- NIR hardware extension (webcam + IR-pass filter + 940 nm LED grid + OTG cable)

---

## Building

1. Clone the repository
2. Ensure `local.properties` contains valid paths (or set them as environment variables):

```properties
sdk.dir={path to Android SDK}
ndk.dir={path to Android NDK}
```

3. Build with Gradle:

```bash
./gradlew assembleDebug
```

> You may also need to set the `JAVA_HOME` environment variable pointing to your JDK directory.

---

## Future Work

- **Hybrid NIR + Ultrasound** — Combining NIR imaging with ultrasound for deeper vein visualization
- **Burns assessment** — Leveraging NIR imaging to assess burn depth
- **Skin abnormalities recognition** — Using machine learning for automated detection
- **Machine learning-based vein segmentation** — Training models for automatic vein recognition and highlighting

---

## Acknowledgements

This project is based on [OpenCVwithUVC](https://github.com/saki4510t/OpenCVwithUVC) by saki4510t, which provides UVC camera access with OpenCV on non-rooted Android devices. The UVC camera library originates from the [UVCCamera](https://github.com/saki4510t/UVCCamera) repository.

---

## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

&nbsp;&nbsp;&nbsp;&nbsp;http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

All files in the folder are under this Apache License, Version 2.0.
Files in the `jni/opencv3` folders have a different license — see the respective files.
