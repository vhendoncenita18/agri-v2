# Interactive Variety Selector in 3D Model Viewer

## Problem Statement

Users need an interactive way within the 3D model viewer (`LettuceScene.kt`) to switch between different lettuce varieties (e.g., **Olmetie**, **Limambo**, **Orbital**) so they can inspect variety-specific 3D models.

**Goal:**
Add an interactive variety selector button/bar to `LettuceScene` that allows users to seamlessly toggle between lettuce varieties, dynamically loading the corresponding 3D model using `PlantUtils.getModelPathForVarietyAndStage`.

## Proposed Changes

### 1. Visualization Component (`LettuceScene.kt`)

#### [MODIFY] [LettuceScene.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/visualization/threeD/LettuceScene.kt)
- Add new parameters:
  - `enableVarietySelector: Boolean = false`
  - `initialVariety: String = "Olmetie"`
  - `onVarietySelected: (String) -> Unit = {}`
- Implement an interactive glassmorphic variety selection bar (supporting varieties: **Olmetie**, **Limambo**, **Orbital**) positioned elegantly in the 3D viewer.
- Dynamically resolve model path using `PlantUtils.getModelPathForVarietyAndStage(selectedVariety, activeStage)`.

## Verification Plan

### Automated Tests
- Gradle build `app:assembleDebug` to verify compilation.

### Manual Verification
- Deploy app and verify the interactive variety selector buttons appear in the 3D viewer when enabled, and switch 3D models correctly.
