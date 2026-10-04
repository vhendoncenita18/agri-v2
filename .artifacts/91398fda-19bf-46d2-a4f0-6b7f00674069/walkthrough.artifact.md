# Walkthrough - Heading & Maturity Size Adjustment

## Overview

Successfully decreased the 3D model scale slightly for the **Heading** and **Maturity** stages in `PlantUtils.kt` so they fit comfortably within the viewer boundaries.

## Changes

### [PlantUtils.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/ui/utils/PlantUtils.kt)
- Adjusted `heading` scale from `2.2f` to `1.8f`.
- Adjusted `maturity` scale from `2.6f` to `2.1f`.

## Verification Results

### Automated Tests
- Gradle build `app:assembleDebug` completed successfully with zero compilation errors.
