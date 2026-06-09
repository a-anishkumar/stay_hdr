# 💧 HydraFlow AI

[![Android Platform](https://img.shields.io/badge/Platform-Android%20%2F%20Wear%20OS-3DDC84?style=for-the-badge&logo=android)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose)](https://developer.android.com/compose)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange?style=for-the-badge)](https://developer.android.com/about/versions/oreo)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](LICENSE)

> A state-of-the-art, AI-powered hydration tracking ecosystem designed to keep your health in perfect balance. Seamlessly integrated across Android Mobile, Wear OS Smartwatches, and Home Screen Widgets.

---

## 🌟 Key Features

### 📱 Android Mobile App
*   **Dynamic AI Calibration**: Water intake goals are dynamically adjusted using a specialized algorithm incorporating your profile (Weight, Height, Age, Activity Level) and real-time local weather factors (Temperature, Humidity, Season).
*   **Smart Logging Dashboard**: Quick-log predefined beverages (Water, Coffee, Tea, Juice, Soda) each with custom hydration indexing scores (e.g., caffeine offsets, sugar absorption delays).
*   **AI Coach & Insights**: Predicts next optimal intake times, tracks dehydration risk levels, and generates personalized recommendations based on lifestyle choices.
*   **Interactive Analytics**: Explore weekly and monthly consumption trends with beautiful graphs and detailed charts.
*   **Gamification**: Earn daily streaks, set custom target milestones, and unlock trophies for hitting hydration goals.
*   **Persistent & Secure**: Seamless, lightweight database schema with SQLite-based architecture.

### ⌚ Wear OS Companion
*   **Standalone Hydration Glance**: Fast-loading, optimized circular UI showing progress towards your daily goal.
*   **Quick Logging**: Double-tap or tap buttons to log `+250ml` of water directly from your wrist.
*   **Smart Sync**: Real-time mock synchronization back to your central database to ensure all records match.

### 🖼️ Home Screen Widget
*   **At-A-Glance Status**: Custom widget showing your daily water percentage progress without needing to open the app.
*   **Instant Log**: Quick log water with one tap from the widget.

---

## 🛠️ Tech Stack & Architecture

HydraFlow AI is built using the latest best practices in modern Android Development:

*   **Architecture**: MVVM (Model-View-ViewModel) pattern with cleanly separated repository layers.
*   **UI Framework**: 100% Jetpack Compose for both Mobile and Wear OS modules.
*   **Navigation**: Jetpack **Navigation 3** (`androidx.navigation3`) for robust, type-safe screen routing.
*   **Database**: Custom lightweight SQLite management layer (`HydraSQLiteHelper`) mapped to robust DAOs (`HydraDao`).
*   **Background Tasks**: Android Jetpack **WorkManager** for scheduled tasks like daily resets, backups, and notifications.
*   **APIs**:
    *   **Weather Service**: Integrates temperature, humidity, and seasonal changes to influence cellular hydration needs.
    *   **Health Connect Client**: Configured for cross-app wellness data-sharing ecosystem.

---

## 🧠 How the AI Goal Calculation Works

The application does not use static guidelines. It employs a comprehensive dynamic water estimation formula:

$$\text{Daily Goal (ml)} = (\text{Weight}_{\text{kg}} \times 35) + \text{Height}_{\text{adj}} + \text{Age}_{\text{adj}} + \text{Weather}_{\text{adj}} + \text{Activity}_{\text{adj}}$$

### Adjustment Parameters:
1.  **Physical Dimensions**: Height adjustments ($+10\text{ml}$ per cm above $170\text{cm}$) and age scaling.
2.  **Activity Level**:
    *   *Sedentary*: $+0\text{ml}$
    *   *Moderate (Active)*: $+400\text{ml}$
    *   *High (Athletic)*: $+800\text{ml}$
3.  **Local Weather**:
    *   *Temperature*: $+30\text{ml}$ for every degree Celsius above $22^\circ\text{C}$ (up to $+1000\text{ml}$).
    *   *Humidity*: $+250\text{ml}$ in highly humid environments ($>70\%$ humidity) to compensate for sweat rate.
    *   *Season*: Adjusts goal according to temperature baselines (e.g., Summer $+300\text{ml}$, Winter $-200\text{ml}$).

---

## 📁 Project Structure

```
stay_hdr/
├── app/                  # Android Mobile Application Module
│   └── src/main/java/com/example/hydraflowai/
│       ├── data/         # Repositories, SQLite DB, Weather Service, AI Engine
│       ├── theme/        # Custom Compose Theme & Harmonious Color Palette
│       └── ui/           # Onboarding, Dashboard, Analytics, Streaks, AI Screens
├── wear/                 # Wear OS Companion Module
│   └── src/main/java/com/example/hydraflowai/wear/
│       └── MainActivity  # Circular Wear OS UI & Quick-Log Sync Action
├── gradle/               # Version catalogs (libs.versions.toml) and wrappers
└── build.gradle.kts      # Project-level Gradle build configuration
```

---

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug (or newer)
*   JDK 17 or higher
*   Android SDK 36 (targetSdk)
*   Min SDK 26 (Android 8.0+)

### Building from Source

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/a-anishkumar/stay_hdr.git
    cd stay_hdr
    ```

2.  **Open in Android Studio**:
    *   Select **File > Open** and choose the `stay_hdr` directory.
    *   Let Gradle sync finish.

3.  **Run the Mobile App**:
    *   Select the `app` run configuration and target an Android emulator or physical device.

4.  **Run the Wear OS App**:
    *   Select the `wear` run configuration and deploy to a Wear OS emulator.

---

## 🛡️ License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
