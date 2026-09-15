# Implementation Plan - Persistent Notifications & Enhanced Validation

This plan outlines the steps to implement persistent in-app notifications synchronized with system alerts and add specific growth stage validation to the plant verification form.

## User Review Required

> [!IMPORTANT]
> A new `NotificationEntity` table will be added to the Room database. This will require a database migration (handled via `fallbackToDestructiveMigration` for this development phase).

## Proposed Changes

### [Data Layer]

#### [NEW] [NotificationEntity.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/data/local/entities/NotificationEntity.kt)
- Create a Room entity to store notifications with fields: `id`, `title`, `message`, `timestamp`, `isSystemNotification`, `isRead`, and `type`.

#### [NEW] [NotificationDao.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/data/local/dao/NotificationDao.kt)
- Define Room DAO with:
    - `getAllNotifications()` (Flow)
    - `insertNotification()`
    - `updateNotification()`
    - `markAllAsRead()`

#### [NEW] [NotificationConverters.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/data/local/database/converters/NotificationConverters.kt)
- Add converters for `NotificationType` enum to string and back.

#### [MODIFY] [AgrinovisDatabase.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/data/local/database/AgrinovisDatabase.kt)
- Add `NotificationEntity` to the entities list.
- Register `NotificationConverters`.
- Add `abstract fun notificationDao(): NotificationDao`.

#### [NEW] [NotificationRepository.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/data/repository/NotificationRepository.kt)
- Implement repository to bridge DAO and ViewModel.

### [Logic Layer]

#### [MODIFY] [HomeViewModel.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/ui/viewmodels/HomeViewModel.kt)
- Initialize `NotificationRepository`.
- Change `_notifications` to collect from repository's Flow.
- Update `addNotification` to save to the database via repository.
- Update `markNotificationsAsRead` to update the database.

### [UI Components]

#### [MODIFY] [NotificationsScreen.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/ui/screens/NotificationsScreen.kt)
- Update `NotificationItemCard` to visually highlight unread notifications (e.g., adding a "NEW" tag or changing border intensity).

#### [MODIFY] [VerificationScreen.kt](file:///C:/Users/Admin/AndroidStudioProjects/Agrinovis/app/src/main/java/com/theis/agrinovis/ui/screens/VerificationScreen.kt)
- Add validation logic: `if (growthStage == "Seedling" && dap > 7)`.
- Display error text: "Seedling stage cannot have a DAP greater than 7 days."
- Disable the "PROCESS PREDICTION" button when validation fails.

## Verification Plan

### Automated Tests
- Run `gradle_build app:assembleDebug` to ensure compilation.

### Manual Verification
1. **Notifications**:
   - Generate a notification (e.g., by creating a plant that is ready for harvest).
   - Check if it appears in the in-app list.
   - Restart the app and verify the notification is still there.
   - View notifications and verify the badge on the home screen disappears.
2. **Validation**:
   - Open `VerificationScreen`.
   - Set stage to "Seedling" and DAP to 8.
   - Verify the error message appears and the button is disabled.
   - Set DAP to 7 and verify the error disappears.
