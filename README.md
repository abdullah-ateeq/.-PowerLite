# PowerLite ⚡

**PowerLite** is a fast, modern, offline Android application for calculating powers, square roots, cube roots, and $n$-th roots with high precision and step-by-step calculation explanations.

Built with **Kotlin** and **Jetpack Compose (Material 3)**.

---

## ✨ Features

- **Power Calculations**: Calculate $x^n$ supporting positive/negative bases and exponents, integers, and decimals.
- **Root Calculations**: Calculate square roots ($\sqrt{x}$), cube roots ($\sqrt[3]{x}$), and $n$-th roots ($\sqrt[n]{x}$).
- **High Precision**: Exact result detection and high-precision root calculations.
- **Step-by-Step Explanations**: Visual breakdown showing how each calculation is derived.
- **History & Management**: Save, view, copy, and manage past calculation history.
- **Copy & Share**: Easily copy or share calculation results with a single tap.
- **Privacy First**: 100% offline operation — no internet required, all data stays securely on your device.
- **Modern UI**: Built with Material 3, supporting Light Theme, Dark Theme, and Dynamic Colors.
- **Haptic Feedback**: Tactile responses during user interactions.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 Design
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Domain Layer
- **Navigation**: Navigation Compose
- **State Management**: Kotlin `StateFlow` & `ViewModel`
- **Target SDK**: 37 | **Min SDK**: 26 (Android 8.0+)

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (Ladybug or newer recommended)
- JDK 11 or higher
- Android SDK 37

### Building from Source

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/.-PowerLite.git
   cd .-PowerLite
   ```

2. **Open in Android Studio** and sync Gradle dependencies.

3. **Build the Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🧪 Running Tests

PowerLite contains unit tests for mathematical domain engines, validators, parsers, and repositories:

```bash
./gradlew test
```

---

## 📄 License

```
Copyright 2025 PowerLite

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
