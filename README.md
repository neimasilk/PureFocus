# PureFocus

**The Fastest, Most Minimalist Focus Writing App for Android**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🎯 Vision

PureFocus is designed with one unwavering principle: **Speed and Simplicity Above All Else**. Every design decision, every line of code, and every feature is evaluated against one question: "Does this make the app faster and simpler?"

## ✨ Features

### 🚀 Lightning Fast Performance
- **< 1 second** app launch time
- **60fps** text input with zero lag
- **< 50MB** memory usage
- **< 10MB** app size

### ✍️ Distraction-Free Writing
- Full-screen, minimalist text editor
- No formatting distractions - pure focus on content
- Automatic text saving every few seconds
- One-tap text copying for easy export

### 🍅 Integrated Pomodoro Timer
- Non-intrusive timer overlay
- Standard 25-minute work sessions
- 5-minute short breaks, 15-30 minute long breaks
- Gentle, non-disruptive notifications
- **Foreground service** ensures timer accuracy in background
- **Persistent notifications** show timer status when app is minimized
- Zero impact on writing performance

### 🎨 Minimal Design
- Clean light/dark themes
- Instantly understandable interface
- Zero learning curve
- Optional word/character count (disabled by default)

## 🏗️ Architecture

**Built for Performance:**
- **Native Android** with Kotlin
- **Jetpack Compose** for modern, efficient UI
- **MVVM Architecture** for clean separation
- **SharedPreferences** for lightning-fast data access
- **Zero third-party UI libraries** for maximum control

## 🚀 Quick Start

### Prerequisites
- Android 7.0 (API 24) or higher
- ~10MB storage space

### Installation
1. Download the APK from [Releases](../../releases)
2. Install on your Android device
3. Launch and start writing immediately

### Development Setup

```bash
# Clone the repository
git clone https://github.com/yourusername/PureFocus.git
cd PureFocus

# Open in Android Studio
# Build and run
```

**Development Requirements:**
- Android Studio (latest stable)
- JDK 11 or higher
- Android SDK 34+
- Physical Android device (recommended for performance testing)

## 📊 Performance Targets

| Metric | Target | Status |
|--------|--------|---------|
| App Launch Time | < 1 second | 🔄 In Development |
| Text Input Latency | < 16ms (60fps) | 🔄 In Development |
| Memory Usage | < 50MB | 🔄 In Development |
| APK Size | < 10MB | 🔄 In Development |
| Battery Impact | Minimal | 🔄 In Development |

## 🛠️ Development Philosophy

PureFocus follows the **"Vibe Coding"** methodology:

1. **Human as Lead Architect** - Strategic planning by humans
2. **AI as Execution Assistant** - Implementation support
3. **Context is King** - Comprehensive project documentation
4. **Baby Steps** - Small, testable development iterations
5. **Living Documentation** - Continuously updated project knowledge

## 📁 Project Structure

```
PureFocus/
├── app/                    # Android application code
│   ├── src/main/kotlin/    # Kotlin source files
│   └── src/main/res/       # Android resources
├── memory-bank/            # Project documentation
│   ├── proposal.md         # Project proposal
│   ├── product-design-doc.md
│   ├── tech-stack.md
│   ├── implementation-plan.md
│   ├── architecture.md
│   ├── progress.md
│   └── vibe-coding.md      # Development methodology
└── README.md               # This file
```

## 🎯 MVP Scope

**What's Included:**
- ✅ Full-screen text editor
- ✅ Integrated Pomodoro timer
- ✅ Auto-save functionality
- ✅ Copy text feature
- ✅ Light/dark themes
- ✅ Basic timer customization

**Intentionally Excluded (for speed and simplicity):**
- ❌ Rich text formatting
- ❌ Multiple documents
- ❌ Cloud sync
- ❌ Complex settings
- ❌ Social features
- ❌ Analytics or tracking

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run UI tests
./gradlew connectedAndroidTest

# Performance testing
# Use Android Studio Profiler for continuous monitoring
```

## 📈 Development Progress

- **Phase 0:** Initial Setup ✅
- **Phase 1:** Core Text Editor 🔄
- **Phase 2:** Pomodoro Integration ⏳
- **Phase 3:** Settings & Polish ⏳
- **Phase 4:** Testing & Validation ⏳

See [progress.md](memory-bank/progress.md) for detailed development tracking.

## 🤝 Contributing

PureFocus prioritizes **performance and simplicity** above all else. Contributions must:

1. **Maintain or improve performance targets**
2. **Reduce complexity, not add it**
3. **Include performance benchmarks**
4. **Follow the established architecture**
5. **Update documentation accordingly**

### Contribution Process
1. Read the [implementation plan](memory-bank/implementation-plan.md)
2. Check current [progress](memory-bank/progress.md)
3. Follow the baby-step methodology
4. Include performance validation
5. Update relevant documentation

## 📄 License

MIT License - see [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Cal Newport** - Deep Work methodology inspiration
- **Francesco Cirillo** - Pomodoro Technique
- **Nicolas Zullo & ModernKataKupas** - Vibe Coding methodology inspiration
- **Android Team** - Jetpack Compose framework

## 📞 Contact

For questions about the project philosophy or development approach, please refer to the comprehensive documentation in the `memory-bank/` directory.

---

**"Speed and Simplicity Above All Else"** - PureFocus Development Philosophy

*Built with ❤️ for writers who value focus over features*: The Blazing-Fast Minimalist Focus App

## Project Description

PureFocus is an Android productivity application fanatically engineered for **extreme speed, simplicity, and deep focus**. It's designed to help users concentrate on one essential writing task at a time by providing a hyper-minimalist, distraction-free environment. PureFocus aims to load instantly and respond without any perceptible lag, allowing users to immerse themselves in their work immediately.

The core philosophy is to be an **exceptionally lightweight and intuitively understandable tool** that actively supports single-tasking and deep work principles.

This project is developed following the "Panduan Vibe Coding Indonesia V1.2.1" methodology.

## Core App Philosophy & Principles

* **Blazing Speed & Responsiveness (Absolute Priority):** Engineered for best-in-class startup time and a lag-free user experience.
* **Extreme Minimalism & Immediate Understandability:** The UI is self-explanatory, allowing users to understand its purpose and how to use it within seconds.
* **Single-Tasking (Focus Write Mode):** The MVP exclusively features a minimalist text editor integrated with a Pomodoro timer to enforce focus on one writing task.
* **Distraction-Free Environment:** No unnecessary elements, notifications (except for Pomodoro), or animations.
* **Featherlight Footprint:** Prioritizing minimal resource consumption and a small application size.

## Current Project Status

* **Stage:** Phase 1 - Core Features Implementation (In Progress)
* **Completed Baby Steps:**
  * ✅ Baby Step 1: Notifications - Basic notification system for focus session end
  * ✅ Baby Step 2: Settings UI - Simple settings screen with focus duration input
  * ✅ Baby Step 3: Focus Write Text Logging - Text logging from FocusWriteScreen
  * ✅ Baby Step 4: Foreground Service - PomodoroService with persistent notifications
* **Next Step:** Baby Step 5: Instrumented UI Tests for FocusWriteScreen

## Initial Target Platform

* Android

## Core Feature (MVP)

* **Focus Write Mode:** A hyper-minimalist, full-screen text editor optimized for fluid typing and rendering, seamlessly integrated with a performance-conscious Pomodoro timer.
    * Text Storage: Automatic saving to `SharedPreferences` (single, most recent draft). Primary retrieval via "copy text".
    * Customization: Basic light/dark theme, Pomodoro duration settings, and an optional (default-OFF) unobtrusive word/character count.

## Key Technology Highlights (MVP)

* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose (with strict adherence to performance best practices and continuous profiling).
* **Performance Focus:** Minimal dependencies (no third-party UI component libraries for MVP), ProGuard/R8, Baseline Profiles.
* **Local Data Storage:** `SharedPreferences` for lean data handling.

## Development Methodology

This project adheres to the **"Panduan Vibe Coding Indonesia V1.2.1"** framework, emphasizing:
* Human as the strategic planner, AI as a competent implementation assistant.
* Context-rich "Memory Bank" for AI.
* Iterative development via "Baby Steps".
* Continuous testing and living documentation.

## Primary Development Environment

* **Project Management & Documentation IDE:** Visual Studio Code
* **Android Development IDE:** Android Studio
* **Version Control:** Git & GitHub

## How to Contribute / Run

*(This section will be updated with build instructions and contribution guidelines as the project progresses.)*

## Memory Bank

All core planning documents (including the definitive Project Proposal, PDD, Tech Stack, MVP Implementation Plan), status updates, and detailed implementation steps (`baby-step.md` files) are located in the `/memory-bank` directory within this repository. This aligns with the Vibe Coding guide's requirements for a comprehensive and up-to-date project context.

---
*This README.md was drafted based on the definitive Project Proposal (Version 1.2) for the PureFocus application.*
