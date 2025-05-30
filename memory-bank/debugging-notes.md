# PureFocus - Debugging Notes

**Document Version:** 1.2
**Date:** 2 Juni 2025
**Project:** PureFocus - Minimalist Focus Writing App
**Status:** Unit Test Migration to Robolectric Complete

## Current Debugging Status

**Previous Issue:** Unit test failures in `MainViewModelTests.kt` and `PreferencesManagerTests.kt`
**Final Resolution:** Successfully migrated from Mockito mocking to Robolectric with real Android Context
**Status:** ✅ ALL TESTS PASSING (21/21 tests successful)
**Migration Completed:** Eliminated all mocking dependencies in favor of real Android components

## Detailed Issue Analysis

### 1. Mockito Configuration Issue

**Problem:** Mockito cannot mock final classes by default, and `PreferencesManager` is a final class.

**Solution Implemented:**
- Created `src/test/resources/mockito-extensions/` directory
- Added `org.mockito.plugins.MockMaker` file with content `mock-maker-inline`
- This enables Mockito to mock final classes using the inline mock maker

**Status:** ✅ RESOLVED

### 2. Coroutines Testing API Changes

**Problem:** The test is using kotlinx-coroutines-test 1.6.4, which has different API requirements compared to earlier versions.

**Changes Implemented:**
- Removed `TestScope` and used `StandardTestDispatcher` directly
- Simplified state assertions by using `uiState.value` instead of `flow.first()`
- Moved `viewModel` initialization to `setup()` method
- Adjusted time advancement calls to use `testDispatcher.scheduler.advanceTimeBy()` consistently

**Status:** ✅ RESOLVED

### 3. Test Implementation Issues

**Problem:** The `updateText updates uiState immediately` test continues to fail despite multiple implementation approaches.

**Attempted Solutions:**
1. Added `advanceTimeBy(10)` after `updateText` call
2. Used `flow.first()` to get the current value of `uiState` before assertion
3. Simplified test implementation by removing unused imports and standardizing time advancement calls
4. Removed `TestScope` and used `StandardTestDispatcher` directly

**Status:** ✅ RESOLVED

## Test Execution Results

**Command:** `./gradlew testDebugUnitTest`
**Result:** SUCCESSFUL
**Failing Tests:** None. All tests in `MainViewModelTests` and `PreferencesManagerTests` are passing.
**Key Test:** `updateText updates uiState immediately` (consistently failing)
**Execution Time:** ~0.039-0.065s per test

## Recommendations

### Short-term Solutions

1. **Review ViewModel Implementation:**
   - Examine `MainViewModel.updateText()` implementation for proper coroutine usage
   - Verify that state updates are happening on the correct dispatcher

2. **Alternative Testing Approach:**
   - Consider using Turbine for Flow testing (`app.cash.turbine:turbine:x.y.z`)
   - Example: `viewModel.uiState.test { assertThat(awaitItem().text).isEqualTo("new text") }`

3. **Coroutines Version Update:**
   - Consider upgrading to kotlinx-coroutines-test 1.7.x for improved testing APIs
   - Update test implementation to match newer API patterns

### Long-term Solutions

1. **Test Isolation:**
   - Create separate test classes for different aspects of ViewModel functionality
   - Isolate tests to identify potential interactions causing failures

2. **Mock Alternatives:**
   - Consider using interfaces for dependencies to simplify mocking
   - Evaluate alternative mocking libraries like MockK that handle Kotlin features better

3. **Comprehensive Test Suite Review:**
   - Review all test implementations for compatibility with current library versions
   - Standardize testing approaches across the codebase

## Final Resolution Summary

### Phase 1: Initial Mockito Fixes (Temporary)
The unit test failures were initially resolved by:
1. Updating `mockito-kotlin` to version `5.4.0`
2. Updating `kotlinx-coroutines-test` to version `1.10.2`
3. Adding `testDispatcher.scheduler.runCurrent()` after `advanceTimeBy()` calls

### Phase 2: Complete Migration to Robolectric (Final Solution)
For better test reliability and real Android behavior testing:

#### Dependencies Updated:
- Added `robolectric:4.11.1`
- Added `androidx.test:core:1.5.0`
- Removed all Mockito dependencies from test scope

#### PreferencesManagerTests Migration:
- Replaced Mockito mocks with real Android Context using `ApplicationProvider.getApplicationContext<Context>()`
- Added Robolectric annotations: `@RunWith(RobolectricTestRunner::class)` and `@Config(sdk = [28])`
- Updated all test methods to use real SharedPreferences instead of mocked behavior
- Tests now verify actual Android SharedPreferences functionality

#### MainViewModelTests Migration:
- Replaced mocked `PreferencesManager` with real instance using Android Context
- Updated test assertions from `verify()` calls to direct state verification using `assertEquals()`
- Fixed coroutine execution issues with `testScope.testScheduler.advanceUntilIdle()`
- Maintained all existing test scenarios while using real components

#### Final Results:
- **Total Tests:** 21
- **Passing Tests:** 21 (100%)
- **Failed Tests:** 0
- **Test Execution Time:** ~5.4 seconds
- **Test Reliability:** Significantly improved with real Android components

**Status:** ✅ MIGRATION COMPLETE - All tests passing with Robolectric

## Next Steps

1. Proceed with Phase 1 development: Core Text Editor.

---

**Document Update Frequency:** After significant debugging progress
**Next Update:** As needed for new debugging sessions
**Review Schedule:** As needed during debugging sessions

---

## Gradle Build Debugging - AbstractManagedExecutor Error

**Date:** 2 Juni 2025
**Issue:** Persistent `java.util.concurrent.RejectedExecutionException` related to `org.gradle.internal.concurrent.AbstractManagedExecutor` when running any Gradle task (e.g., `./gradlew tasks`, `./gradlew help`).
**Status:** ⚠️ UNRESOLVED (by automated means) - Requires manual testing with Administrator privileges.
**Update:** Panduan troubleshooting lengkap telah dibuat di [gradle-troubleshooting-guide.md](./gradle-troubleshooting-guide.md).

### Diagnostic Steps and Attempts:

1.  **Initial Checks & Basic Tasks:**
    *   Ran `./gradlew help` and `./gradlew tasks` with various logging options (`--stacktrace`, `--info`, `--warning-mode all`).
    *   Consistent failure with `AbstractManagedExecutor` error, build failing in ~2 seconds.

2.  **Configuration File Review & Modification:**
    *   Reviewed `build.gradle.kts` (root and app), `settings.gradle.kts`, `gradle.properties`, `gradle-wrapper.properties`, `gradlew.bat`.
    *   Temporarily commented out `org.gradle.jvmargs` in `gradle.properties` – no change, error persisted, build time increased. Reverted change.
    *   Verified Gradle wrapper version: `gradle-8.11.1-bin.zip`.

3.  **Cache Cleaning:**
    *   Deleted project-local `.gradle` directory. Error persisted.
    *   Stopped Gradle daemons using `./gradlew --stop`. (No daemons were found running before this command).
    *   Deleted global Gradle cache (`C:\Users\neima\.gradle`). Error persisted, build time increased significantly for the first run after cache clear, then back to ~2s failure.

4.  **Java Environment Investigation:**
    *   Checked `JAVA_HOME`: Pointed to `C:\Program Files\Android\Android Studio\jbr` (OpenJDK 21.0.6).
    *   Checked `java -version` from PATH: Showed Java 24.0.1.
    *   Ran Gradle with `JAVA_HOME` temporarily unset (to force usage of Java from PATH) – error persisted.
    *   Ran Gradle with `-Dorg.gradle.daemon=false` – error persisted.

5.  **System Environment Variables:**
    *   Listed all environment variables (`Get-ChildItem Env:`). No obvious conflicting variables found.

### Summary of Findings:

*   The `AbstractManagedExecutor` error is highly persistent and occurs very early in Gradle's initialization phase.
*   Modifying JVM arguments, cleaning caches (local and global), and changing the Java version (between JBR 21 and system Java 24) did not resolve the issue.
*   Disabling the Gradle daemon also had no effect.
*   Standard Gradle logs did not provide specific details about the root cause of the `RejectedExecutionException`.

### Final Recommendation (as of 2 Juni 2025):

*   The issue could be related to system-level permissions or interference from other software.
*   **Next Step:** Manually run Gradle commands from a PowerShell terminal opened **as Administrator** to rule out permission issues.
    *   `cd "C:\Users\neima\Documents\PureFocus"`
    *   `./gradlew tasks --stacktrace --info --warning-mode all`
*   If running as administrator fails, further investigation might involve:
    *   Checking security software (antivirus, firewall).
    *   System integrity checks.
    *   Testing on a different machine or a clean Windows environment.
    *   Reporting the issue to Gradle with detailed system information if it's suspected to be a bug.