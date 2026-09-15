# Implementation Plan - Enhancing Bottom Navigation Clickability

This plan ensures that navigation bar labels are explicitly clickable and correctly trigger navigation, satisfying the user's request to include the label in the clickable part of the nav bar.

## Proposed Changes

### [UI Components]

#### [MODIFY] [AgrinovisScaffold.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/ui/components/AgrinovisScaffold.kt)
- Refactor the navigation logic in `AgrinovisBottomBar` into a reusable lambda.
- Apply this navigation lambda to the `NavigationBarItem`'s `onClick` parameter.
- Add `Modifier.clickable` to the `Text` composable within the `label` slot, passing the same navigation lambda.
- Ensure the `Text`'s clickable modifier does not show a redundant ripple by setting `indication = null`.

## Verification Plan

### Automated Tests
- Build the project using `./gradlew :app:assembleDebug` to verify no compilation errors.

### Manual Verification
- Deploy the app.
- Tap the **labels** (e.g., "Home", "Monitor", "Insights", "Profile") in the bottom navigation bar.
- Verify that the app correctly navigates to the corresponding screen.
- Tap the **icons** and verify they still work as expected.
