# Variety- and Growth-Stage 3D Model Rendering in Monitor and Commit Archive

## Problem Statement

When users add a lettuce plant, the app should display the 3D model based on its specific **variety** and **growth stage** (`PlantUtils.getModelPathForVarietyAndStage`) across:
1. The **Monitor** screen (Plant Detail / Plant Cards).
2. The **Commit Archive** screen (Harvested records detail dialog).

**Goal:**
Ensure that whenever a user views a plant in the Monitor screen or inspects an archived/harvested record in the Commit Archive screen, the exact 3D model matching its recorded variety and growth stage is rendered.

## Proposed Changes

### 1. Monitor Screen / Plant Detail (`PlantDetailScreen.kt`, `HolographicPlantPreview.kt`)

#### [MODIFY] [PlantDetailScreen.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/ui/screens/PlantDetailScreen.kt)
- Update `LettuceScene` modelPath from `PlantUtils.getModelPathForStage(plant!!.stage)` to `PlantUtils.getModelPathForVarietyAndStage(plant!!.variety, plant!!.stage)`.

#### [MODIFY] [HolographicPlantPreview.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/ui/components/HolographicPlantPreview.kt)
- Add `variety: String = "Olmetie"` parameter and display the variety name alongside the stage icon in list cards.

### 2. Commit Archive Screen (`HarvestedLogScreen.kt`)

#### [MODIFY] [HarvestedLogScreen.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/ui/screens/HarvestedLogScreen.kt)
- Add `LettuceScene` 3D model viewer to `HistoryDetailDialog` using `PlantUtils.getModelPathForVarietyAndStage(plant.variety, plant.stage)` so harvested records in the Commit Archive display their specific variety and growth stage 3D model.

## Verification Plan

### Automated Tests
- Gradle build `app:assembleDebug` to verify compilation.

### Manual Verification
- Add a plant with a specific variety and stage, open it in Monitor/Plant Detail, and verify the correct 3D model.
- Archive/harvest a plant, open its record in Commit Archive, and verify the 3D model renders correctly in the dialog.
