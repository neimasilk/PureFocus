# PureFocus: The Blazing-Fast Minimalist Focus App

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

* **Stage:** Stage 0: Initial Project Setup & Document Planning (Completed, as per Vibe Coding).
* **Next Step:** Stage 1: Environment Setup & Implementation Plan Clarification.

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
