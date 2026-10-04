# TFLite Machine Learning Model Specifications

This directory contains the TensorFlow Lite (`.tflite`) deployment assets for the **Agrinovis Lettuce Harvest Prediction System**.

---

## 1. Computer Vision Model (`cv_model.tflite`)

- **Filename**: `cv_model.tflite`
- **Location**: `app/src/main/assets/models/cv_model.tflite`
- **Input Shape**: `[1, 224, 224, 3]` (Float32 Tensor, RGB)
- **Output Shape**: `[1, 3]`
  - Output Index `0`: `diameterCm` (Float32, Physical diameter in centimeters)
  - Output Index `1`: `heightCm` (Float32, Physical height in centimeters)
  - Output Index `2`: `confidence` (Float32, Model detection confidence score 0.0 - 1.0)
- **Kotlin Integration Engine**: `com.theis.agrinovis.ml.cv.TFLiteCvEngine`

---

## 2. Harvest Prediction Model (`harvest_prediction_model.tflite`)

- **Filename**: `harvest_prediction_model.tflite`
- **Location**: `app/src/main/assets/models/harvest_prediction_model.tflite`
- **Input Tensor Shape**: `[1, 8]` (Float32 Vector)
  - Feature Index `0`: `DAP` (Days After Planting, Float32)
  - Feature Index `1`: `leafCount` (Float32)
  - Feature Index `2`: `diameterCm` (Float32)
  - Feature Index `3`: `heightCm` (Float32)
  - Feature Index `4`: `temperature` (°C, Float32)
  - Feature Index `5`: `humidity` (%, Float32)
  - Feature Index `6`: `windSpeed` (m/s, Float32)
  - Feature Index `7`: `pressure` (hPa, Float32)
- **Output Tensor Shape**: `[1, 1]`
  - Output Index `0`: `daysToHarvest` (Numerical target Y, Float32)
- **Kotlin Integration Engine**: `com.theis.agrinovis.ml.prediction.TFLitePredictionEngine`

---

## 3. How to Deploy Trained Models

When the research team finishes training and exporting the `.tflite` models:

1. Export the Computer Vision model as `cv_model.tflite` and copy it to this directory (`app/src/main/assets/models/`).
2. Export the Harvest Prediction regression model as `harvest_prediction_model.tflite` and copy it to this directory (`app/src/main/assets/models/`).
3. Rebuild the Android application (`app:assembleDebug`). The TFLite execution engines will automatically detect the assets and run inference.

---

## 4. 3D Plant Model Asset Roadmap (4 Models per Variety)

In future releases, individual `.glb` 3D model files will be added for each lettuce variety across each growth stage. Each variety will have 4 dedicated 3D models (one per growth stage), totaling **12 3D `.glb` model files**:

### Variety 1: Olmetie (4 3D Models)
- `models/olmetie_seedling.glb`
- `models/olmetie_vegetative.glb`
- `models/olmetie_heading.glb`
- `models/olmetie_maturity.glb`

### Variety 2: Limambo (4 3D Models)
- `models/limambo_seedling.glb`
- `models/limambo_vegetative.glb`
- `models/limambo_heading.glb`
- `models/limambo_maturity.glb`

### Variety 3: Orbital (4 3D Models)
- `models/orbital_seedling.glb`
- `models/orbital_vegetative.glb`
- `models/orbital_heading.glb`
- `models/orbital_maturity.glb`

### Kotlin Asset Resolution Engine
The resolution function `PlantUtils.getModelPathForVarietyAndStage(variety, stage)` handles automatic path resolution with seamless fallback to general growth stage models (`models/seedling.glb`, `models/vegetative.glb`, etc.).

