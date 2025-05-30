# PureFocus - Debugging Notes

**Document Version:** 1.0
**Date:** 1 Juni 2025
**Project:** PureFocus - Minimalist Focus Writing App
**Status:** Unit Test Issues Resolved

## Current Debugging Status

**Issue:** Unit test failures in `MainViewModelTests.kt`
**Affected Tests:** All tests in `MainViewModelTests`, particularly `updateText updates uiState immediately`
**Root Causes Identified:**
1. Mockito unable to mock final `PreferencesManager` class
2. Coroutines testing API changes in kotlinx-coroutines-test 1.6.4
3. Test implementation approach not compatible with current coroutines version

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

## Resolution Summary

The unit test failures in `MainViewModelTests.kt` were resolved by:
1. Updating `mockito-kotlin` to version `5.4.0`.
2. Updating `kotlinx-coroutines-test` to version `1.10.2`.
3. Adding `testDispatcher.scheduler.runCurrent()` after `advanceTimeBy()` calls in tests involving debounce logic to ensure pending coroutine tasks were executed before assertions.

All tests are now passing, unblocking further development.

## Next Steps

1. Proceed with Phase 1 development: Core Text Editor.

---

**Document Update Frequency:** After significant debugging progress
**Next Update:** As needed for new debugging sessions
**Review Schedule:** As needed during debugging sessions