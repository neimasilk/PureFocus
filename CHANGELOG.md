# PureFocus Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - 2025-01-30

### Added
- `FocusWriteViewModel` with StateFlow-based text management
- Two-way data binding between FocusWriteScreen and FocusWriteViewModel
- Comprehensive unit tests for FocusWriteViewModel (6 test cases)
- UI integration tests for FocusWriteScreen with ViewModel
- Word count and character count functionality in ViewModel
- Clear text functionality through ViewModel

### Changed
- FocusWriteScreen now uses FocusWriteViewModel instead of PomodoroTimerViewModel for text management
- Improved separation of concerns between UI and business logic
- Enhanced text state management with TextFieldValue support

### Fixed
- Better text state management and cursor position handling
- Improved performance through proper StateFlow usage

### Technical
- Completed Baby-Step 1-5 for FocusWriteScreen implementation
- All existing and new tests are passing
- Code follows project conventions and includes proper KDoc documentation

---

## Previous Version Example (Delete or adjust this)

## [1.0.0] - 2024-01-15

### Added
- Initial MVP features:
    - Focus Write Mode with a minimalist interface.
    - Integrated Pomodoro timer with customizable focus and break durations.
    - End-of-session notifications for focus and breaks.
    - Automatic text saving while typing and at the end of focus sessions.
    - Settings screen for timer durations.
    - Basic word and character count.
- Initial project documentation: `README.md`, `architecture.md`, `product-design-doc.md`, `implementation-plan.md`.

### Changed
- Initial project structure created with Android Studio, Kotlin, and Jetpack Compose.

### Fixed
- (No significant bugs were fixed for this initial release as it was the first version).

## [0.1.0] - 2024-01-01

### Added
- PureFocus project initialization.
- Git repository creation.
- Basic concept and product planning.

**Note:** Always update the `[Unreleased]` section with your latest changes. When you are ready to release a new version, replace `[Unreleased]` with the version number and release date, then create a new `[Unreleased]` section above it for subsequent changes.