# SalonSync ✂️✨
### Smart Salon Appointment & Real-Time Dynamic Slot Booking Platform

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%201.9-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase%20Realtime%20DB-orange.svg)](https://firebase.google.com)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

---

## 📌 Project Overview
**SalonSync** is a native Android mobile application engineered to modernize and digitize salon operations. By replacing error-prone paper appointment logs and disorganized phone calls with an automated, synchronized cloud system, SalonSync guarantees **zero double-bookings**, provides complete booking transparency, and offers customers the freedom to choose between traditional **In-Salon** appointments and on-demand **At-Home** grooming services.

---

## ✨ Key Features

### 👤 Customer Experience
* **Service Discovery & Search:** Browse comprehensive hair, skin, spa, and beauty treatment catalogs with category filters and real-time search.
* **Dual Delivery Modes:** Seamlessly toggle between **In-Salon** visits and **At-Home** doorstep grooming with automated address capture and convenience fee calculations.
* **Dynamic Slot Booking:** Real-time visual calendar grid displaying available, booked, and past time slots without scheduling conflicts.
* **Live Appointment Tracking:** Track appointment status progression in real-time (`PENDING` ➔ `CONFIRMED` ➔ `COMPLETED` / `CANCELLED`).
* **Profile Management:** Update personal details, phone number, and access self-service password recovery.

### 💼 Salon Owner / Admin Dashboard
* **Real-time Request Approval Queue:** Instant notifications for incoming customer bookings with one-tap **Confirm Slot** or **Reject** actions.
* **Service Catalog Management:** Full CRUD control to add, edit, update, or delete salon services, durations, and pricing.
* **Automatic Slot Recovery:** When an appointment is rejected or cancelled, reserved slots are immediately freed and returned to the public booking pool.
* **Automated Digital Invoicing:** Generates itemized digital invoices with serial numbers (`INV-XXXXXX`) and tax breakdowns upon treatment completion.

---

## 🏗️ Architecture & Technical Stack

* **Architecture:** Model-View-ViewModel (MVVM) with Unidirectional Data Flow (UDF)
* **Programming Language:** Kotlin (Coroutines, StateFlow, Flows)
* **User Interface:** Jetpack Compose & AndroidX Material 3 (Luxury Dark Theme: Deep Indigo & Metallic Soft Gold)
* **Backend as a Service (BaaS):** Google Firebase
  * **Firebase Authentication:** Email/Password identity & persistent session management
  * **Firebase Realtime Database:** Reactive NoSQL JSON document store with multi-path atomic updates
* **Build System:** Gradle 8.2 (Kotlin DSL)
* **Minimum SDK:** Android 8.0 (API Level 26)

---

## 📁 Project Structure

```text
salonsync/
├── app/
│   ├── build.gradle.kts
│   ├── google-services.json
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/salonsync/app/
│       │   ├── MainActivity.kt
│       │   ├── SalonSyncApp.kt
│       │   ├── data/
│       │   │   ├── model/         # User, Service, Booking, SlotState, Invoice, Settings
│       │   │   └── repository/    # FirebaseManager (Atomic Realtime DB & Auth)
│       │   ├── domain/            # AvailabilityEngine (Dynamic Slot Collision Algorithm)
│       │   ├── ui/
│       │   │   ├── navigation/    # AppNavHost (Role-based Navigation Graph)
│       │   │   ├── screens/
│       │   │   │   ├── auth/      # LoginScreen, RegisterScreen (with Role Toggle)
│       │   │   │   ├── customer/  # CustomerHomeScreen, SlotPickerScreen, BookingSummaryScreen, CustomerBookingsScreen
│       │   │   │   ├── admin/     # AdminDashboardScreen, AdminServicesScreen
│       │   │   │   ├── staff/     # StaffScheduleScreen
│       │   │   │   └── profile/   # ProfileScreen
│       │   │   ├── theme/         # Color, Theme, Type (Luxury Material 3 System)
│       │   │   └── viewmodel/     # AuthViewModel, CustomerViewModel, AdminViewModel, StaffViewModel
│       └── res/                   # Drawables, Launcher icons, Values, XML
├── database.rules.json            # Firebase Realtime Database Security Rules
├── initial_seed_data.json         # Seed catalog for salon services and business settings
└── README.md
```

---

## ⚙️ Availability Engine (Algorithm)
The system calculates required time intervals using the mathematical model:
$$\text{Required Slots} = \left\lceil \frac{\text{Duration in Minutes}}{30} \right\rceil$$

It verifies that all consecutive 30-minute blocks starting from the selected time are unoccupied before enabling reservation, completely eliminating double-bookings.

---

## 🚀 Getting Started

### Prerequisites
* Android Studio Iguana / Ladybug or newer
* JDK 17 LTS
* Android SDK (API 26+)

### Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/fahadkansari123/salonsync.git
   cd salonsync
   ```
2. **Open in Android Studio:**
   * Select `File > Open` and select the `salonsync` directory.
   * Allow Gradle to sync dependencies.
3. **Run on Device or Emulator:**
   * Connect an Android device with USB debugging enabled or launch an Android Virtual Device (AVD).
   * Click **Run** (`Shift + F10`) or execute `./gradlew assembleDebug`.

---

## 👤 Author
* **Fahad Ansari** ([@fahadkansari123](https://github.com/fahadkansari123))
