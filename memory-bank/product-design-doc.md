# PureFocus - Product Design Document (PDD)

**Document Version:** 1.0  
**Date:** May 29, 2025  
**Project:** PureFocus - Minimalist Focus Writing App  
**Based on:** Project Proposal v1.2

## 1. Product Vision

**Vision Statement:** To create the fastest, most responsive, and exceptionally simple mobile writing application that eliminates all distractions and cognitive overhead, enabling users to achieve deep focus and flow state in their writing tasks.

**Core Philosophy:** Speed and simplicity above all else. Every design decision must pass the test: "Does this make the app faster and simpler?"

## 3. Target Pengguna

*   **Pelajar dan Mahasiswa:** Membutuhkan fokus saat belajar atau mengerjakan tugas.
*   **Penulis dan Kreator Konten:** Mencari lingkungan bebas distraksi untuk menulis artikel, blog, atau naskah.
*   **Programmer dan Pekerja Pengetahuan:** Perlu konsentrasi tinggi untuk tugas-tugas yang kompleks.
*   **Siapapun yang ingin meningkatkan produktivitas** dengan mengurangi gangguan digital saat bekerja dengan teks.

### User Stories

*   **Sebagai seorang pelajar,** saya ingin dapat mengatur timer fokus agar saya bisa belajar tanpa gangguan selama periode waktu tertentu, dan mendapatkan notifikasi ketika sesi berakhir sehingga saya bisa istirahat sejenak.
*   **Sebagai seorang penulis,** saya ingin antarmuka yang minimalis tanpa banyak tombol atau menu yang mengganggu, sehingga saya bisa sepenuhnya berkonsentrasi pada tulisan saya.
*   **Sebagai seorang programmer,** saya ingin aplikasi ini ringan dan cepat, serta dapat menyimpan pekerjaan saya secara otomatis sehingga saya tidak khawatir kehilangan progres jika terjadi sesuatu.
*   **Sebagai pengguna umum,** saya ingin dapat dengan mudah mengatur durasi fokus dan istirahat sesuai kebutuhan saya, dan melihat statistik sederhana tentang seberapa banyak waktu fokus yang telah saya capai.

## 3. Core Features (MVP)

### 3.1 Focus Write Mode (Primary Feature)

**Description:** A full-screen, minimalist text editor that launches instantly and provides a distraction-free writing environment.

**Key Components:**
- **Hyper-minimalist Text Editor:**
  - Full-screen text input area
  - Clean, highly legible typography
  - No formatting options (plain text only for MVP)
  - Instant text rendering and response
  - Auto-save to SharedPreferences

- **Integrated Pomodoro Timer:**
  - Non-intrusive timer display
  - Standard 25-minute work sessions
  - 5-minute short breaks
  - 15-30 minute long breaks after 4 sessions
  - Subtle, non-disruptive notifications
  - Timer controls accessible without leaving writing mode

- **Essential Settings (Minimal):**
  - Light/Dark theme toggle
  - Pomodoro duration customization
  - Optional word/character count (off by default)
  - Copy text functionality

### 3.2 Data Management

**Storage Strategy:**
- Single draft storage in SharedPreferences
- Automatic saving every few seconds
- Primary text retrieval via copy function
- No complex file management or multiple documents

## 4. User Experience (UX) Design

### 4.1 User Journey

1. **App Launch:** Instant launch directly into Focus Write Mode
2. **Writing Session:** User begins typing immediately
3. **Pomodoro Integration:** Timer runs in background, provides gentle notifications
4. **Break Management:** App guides user through break periods
5. **Session Completion:** User copies text or continues writing
6. **Exit:** Text automatically saved, ready for next session

### 4.2 Interaction Principles

- **Zero Learning Curve:** App purpose and usage obvious within seconds
- **Minimal Taps:** Maximum 2 taps to reach any functionality
- **Immediate Response:** No loading states or delays
- **Gesture-Friendly:** Support for common text editing gestures
- **Interruption Recovery:** Seamless return to writing after breaks

### 4.3 Visual Design Principles

- **Extreme Minimalism:** Only essential elements visible
- **High Contrast:** Excellent readability in all lighting conditions
- **Calming Palette:** Colors that promote focus and reduce eye strain
- **Typography Focus:** Large, clear, comfortable reading font
- **Breathing Room:** Generous whitespace to reduce visual clutter

## 5. Technical Requirements

### 5.1 Performance Targets

- **App Launch Time:** < 1 second cold start
- **Text Input Latency:** < 16ms (60fps)
- **Memory Usage:** < 50MB during normal operation
- **APK Size:** < 10MB
- **Battery Impact:** Minimal background processing

### 5.2 Platform Requirements

- **Target Platform:** Android (MVP)
- **Minimum SDK:** Android 7.0 (API 24)
- **Target SDK:** Latest stable Android version
- **Architecture:** MVVM with Jetpack Compose
- **Language:** Kotlin

### 5.3 Quality Attributes

- **Reliability:** 99.9% crash-free sessions
- **Responsiveness:** Immediate UI feedback
- **Accessibility:** Support for screen readers and large text
- **Offline Capability:** Full functionality without internet

## 7. Persyaratan Non-Fungsional (Non-Functional Requirements)

*   **Kinerja:**
    *   Waktu startup aplikasi: < 2 detik.
    *   Responsivitas UI: Interaksi pengguna harus terasa instan, tanpa lag yang terlihat.
    *   Penggunaan memori: Tetap rendah, idealnya di bawah 100MB saat penggunaan aktif.
*   **Keandalan:**
    *   Aplikasi harus stabil dan tidak crash selama sesi fokus.
    *   Timer harus akurat dan notifikasi harus dikirim tepat waktu.
    *   Penyimpanan otomatis harus berfungsi dengan andal untuk mencegah kehilangan data.
*   **Kegunaan (Usability):**
    *   Antarmuka harus intuitif dan mudah dipelajari, bahkan untuk pengguna non-teknis.
    *   Pengaturan harus mudah diakses dan dipahami.
*   **Keamanan:**
    *   Data pengguna (teks tulisan) harus disimpan secara lokal dan aman. Tidak ada pengiriman data ke server eksternal tanpa izin eksplisit pengguna (untuk fitur masa depan seperti sinkronisasi cloud).
*   **Pemeliharaan (Maintainability):**
    *   Kode harus bersih, terstruktur dengan baik, dan mudah dimodifikasi atau diperluas di masa mendatang.
    *   Dokumentasi kode yang memadai.
*   **Portabilitas:**
    *   Awalnya fokus pada platform Android. Pertimbangan untuk platform lain (iOS, Desktop) dapat dieksplorasi di masa depan.

## 8. Metrik Keberhasilan (Success Metrics)

## 9. Batasan dan Kendala (Constraints and Limitations)

## 10. Pertimbangan Masa Depan (Future Considerations)

## 9. Risk Assessment

### 9.1 Technical Risks
- **Performance Optimization:** Achieving sub-second launch times
- **Jetpack Compose Learning Curve:** Ensuring optimal implementation
- **Device Compatibility:** Performance across various Android devices

### 9.2 User Adoption Risks
- **Market Acceptance:** Users may expect more features
- **Simplicity Perception:** May be seen as too basic
- **Competition:** Existing apps with established user bases

### 9.3 Mitigation Strategies
- Continuous performance profiling during development
- Early user testing and feedback collection
- Clear communication of app philosophy and benefits
- Focus on superior performance as key differentiator

---

**Document Status:** Living document, to be updated throughout development  
**Next Review:** After Phase 1 completion  
**Approval:** Pending development team review