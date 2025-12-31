# 🌊 FloodWatch
> **Sistem Pemantauan & Peringatan Dini Banjir Berbasis Android (IoT)**

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?style=for-the-badge&logo=kotlin)
![Android Studio](https://img.shields.io/badge/Android%20Studio-Hedgehog-green?style=for-the-badge&logo=android-studio)
![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue?style=for-the-badge&logo=jetpackcompose)
![Firebase](https://img.shields.io/badge/Firebase-Auth%20%7C%20Firestore-orange?style=for-the-badge&logo=firebase)
![Cloudinary](https://img.shields.io/badge/Cloudinary-Image%20Storage-blueviolet?style=for-the-badge&logo=cloudinary)

---

## 📱 Tentang Aplikasi

**FloodWatch** adalah aplikasi Android modern yang dirancang untuk membantu warga dan pemerintah memantau ketinggian air sungai secara *real-time*. Aplikasi ini bertujuan untuk memberikan peringatan dini (Early Warning System) guna meminimalisir dampak bencana banjir.

Dibangun dengan arsitektur **MVVM (Model-View-ViewModel)** dan **Clean Architecture**, aplikasi ini menjamin performa yang stabil, kode yang rapi, dan kemudahan dalam pengembangan lebih lanjut.

---

## ✨ Fitur Unggulan

* 🔐 **Secure Authentication**: Sistem Login & Register aman menggunakan **Firebase Authentication**.
* 🗺️ **Interactive Maps**: Visualisasi lokasi sensor menggunakan **OSMDroid** (OpenStreetMap) dengan fitur *Pin Point Location*.
* 📸 **Cloud Image Storage**: Upload foto profil pengguna yang cepat dan ringan menggunakan **Cloudinary**.
* 📊 **Real-time Monitoring**: Data ketinggian air diperbarui secara langsung menggunakan **Firebase Firestore**.
* 🚨 **Smart Alert System**: Penentuan status otomatis berdasarkan ketinggian air:
    * 🟢 **AMAN** (< 150 cm)
    * 🟡 **WASPADA** (150 - 199 cm)
    * 🔴 **BAHAYA** (≥ 200 cm)
* 🔔 **Notification Center**: Halaman khusus yang memfilter dan menampilkan hanya lokasi dengan status bahaya.
* 👤 **User Profile**: Manajemen profil pengguna dinamis dengan statistik pelaporan dan foto custom.

---

## 🛠️ Teknologi & Library (Tech Stack)

| Kategori | Teknologi | Deskripsi |
| :--- | :--- | :--- |
| **Bahasa** | Kotlin | 100% Kotlin Codebase |
| **UI Framework** | Jetpack Compose | Modern declarative UI toolkit (Material 3) |
| **Architecture** | MVVM + Repository | Clean Architecture Pattern |
| **Dependency Injection** | Dagger Hilt | Manajemen dependensi otomatis |
| **Database** | Firebase Firestore | Cloud NoSQL Database (Real-time) |
| **Authentication** | Firebase Auth | Manajemen user (Email/Password) |
| **Media Storage** | Cloudinary | Penyimpanan & Optimasi Gambar (Unsigned Upload) |
| **Maps** | OSMDroid | Peta Open Source ringan & gratis |
| **Image Loading** | Coil | Loading gambar asinkron yang cepat |

---

## 📂 Struktur Proyek

```text
id.antasari.floodwatch_230104040122
 ├── data
 │    └── repository
 │         ├── SensorRepository.kt      // Logic komunikasi ke Firebase & Hitung Status
 │         └── ReportRepository.kt
 ├── domain
 │    └── model
 │         ├── Sensor.kt                 // Data Class (Model)
 │         └── Comment.kt
 ├── presentation
 │    ├── auth
 │    │    ├── LoginScreen.kt            // Halaman Login
 │    │    └── RegisterScreen.kt         // Halaman Daftar Akun
 │    ├── home
 │    │    ├── HomeScreen.kt             // Dashboard Utama & Peta Kecil
 │    │    └── HomeViewModel.kt          // State Management Home
 │    ├── navigation
 │    │    └── NavGraph.kt               // Peta Navigasi Seluruh Aplikasi
 │    ├── notification
 │    │    └── NotificationScreen.kt     // List Peringatan Bahaya
 │    ├── profile
 │    │    ├── ProfileScreen.kt          // Halaman Profil & Upload ke Cloudinary
 │    │    ├── AccountSettingsScreen.kt  // Edit Nama/Info Akun
 │    │    ├── AboutAppScreen.kt         // Info Aplikasi
 │    │    └── HelpSupportScreen.kt      // FAQ & Kontak
 │    │    └── ProfileViewModel.kt       // Hitung Statistik Laporan
 │    └── sensor
 │         ├── AddSensorScreen.kt        // Input Data + Peta Besar
 │         ├── AddSensorViewModel.kt     // Logic Input
 │         └── SensorListScreen.kt       // Pencarian & List Semua Sensor
 ├── ui.theme                            // Tema, Warna, & Tipografi
 ├── FloodWatchApp.kt                    // Base Application (@HiltAndroidApp)
 └── MainActivity.kt                     // Entry Point (@AndroidEntryPoint)

```

## 🚀 Cara Menjalankan (Installation)

### 1. Clone Repositori

```bash
git clone https://github.com/username-kamu/FloodWatch.git
```

### 2. Setup Firebase (Wajib)

Aktifkan Authentication & Firestore, lalu letakkan `google-services.json` di folder `app/`.

```javascript
allow read, write: if request.auth != null;
```

### 3. Setup Cloudinary

Gunakan **Unsigned Upload Preset** bernama `floodwatch_preset` dan sesuaikan Cloud Name.

```kotlin
initCloudinary(context, "CLOUD_NAME_KAMU")
.unsigned("floodwatch_preset")
```

### 4. Build & Run

Sync Gradle dan jalankan di Emulator atau Device.

---
| Halaman Profil | Edit Profil |
| --- | --- |
| <img src="docs/profile_screen.png" width="250" /> | <img src="docs/edit_screen.png" width="250" /> |
## 👨‍💻 Pengembang

**Husin Nafarin Ramadhani**  
Universitas Islam Negeri Antasari Banjarmasin  

© 2025 Husin Nafarin Ramadhani. All Rights Reserved.
