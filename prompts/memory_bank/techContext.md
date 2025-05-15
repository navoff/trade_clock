# Technical Context: TradeClock Android App

## Technologies Used

### Core Technologies

| Technology | Purpose | Description |
|------------|---------|-------------|
| **Kotlin** | Primary programming language | Modern, concise language officially supported by Android |
| **Jetpack Compose** | UI framework | Declarative UI toolkit for building native Android UI |
| **Android SDK** | Platform APIs | Core Android platform functionality |
| **Gradle** | Build system | Manages dependencies and build process |

### Architecture Components

| Component | Purpose | Description |
|-----------|---------|-------------|
| **ViewModel** | UI state management | Manages UI-related data in a lifecycle-conscious way |
| **StateFlow** | Reactive state holder | Kotlin Flow that represents a state with read-only public interface |
| **Room** | Local database | SQLite abstraction layer with compile-time verification |
| **Hilt** | Dependency injection | Simplified version of Dagger for Android |
| **DataStore** | Preferences storage | Modern replacement for SharedPreferences |

### Time Management

| Technology | Purpose | Description |
|------------|---------|-------------|
| **ThreeTenABP** | Time handling | Android backport of java.time (JSR-310) |
| **ZoneId** | Time zone representation | Identifies time zone for each exchange |
| **LocalTime** | Time representation | Represents time without date or time zone |
| **ZonedDateTime** | Time with zone | Represents date-time with time zone context |

### Asynchronous Programming

| Technology | Purpose | Description |
|------------|---------|-------------|
| **Coroutines** | Asynchronous code | Kotlin's solution for asynchronous programming |
| **Flow** | Reactive streams | Cold asynchronous data stream that sequentially emits values |
| **StateFlow** | State container | Hot Flow that represents a state |
| **BroadcastReceiver** | System events | Android component for receiving system broadcasts |

## Development Setup

### Environment

- **IDE**: Android Studio
- **Build System**: Gradle with Kotlin DSL
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 34 (Android 14)
- **Compile SDK**: API 34 (Android 14)

### Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/navoff/tradeclock/
│   │   │   ├── data/                  # Data layer
│   │   │   │   ├── database/          # Room database
│   │   │   │   ├── repositories/      # Repository implementations
│   │   │   │
│   │   │   ├── domain/                # Domain layer
│   │   │   │   ├── models/            # Domain models
│   │   │   │   ├── repositories/      # Repository interfaces
│   │   │   │   ├── usecases/          # Use cases
│   │   │   │
│   │   │   ├── presentation/          # Presentation layer
│   │   │   │   ├── components/        # Reusable UI components
│   │   │   │   ├── screens/           # App screens
│   │   │   │   ├── viewmodels/        # ViewModels
│   │   │   │
│   │   │   ├── di/                    # Dependency injection
│   │   │   ├── ui/                    # UI theme
│   │   │   ├── MainActivity.kt        # Main activity
│   │   │   └── TradeClockApplication.kt # Application class
│   │   │
│   │   ├── res/                       # Resources
│   │   └── AndroidManifest.xml
```

## Technical Constraints

1. **Offline-First**: The app must function without internet connectivity
2. **Battery Efficiency**: Time updates must be efficient to minimize battery usage
3. **Accurate Time Handling**: Must correctly handle time zones and DST changes
4. **Lifecycle Awareness**: Must properly handle Android activity lifecycle
5. **Memory Efficiency**: Should work well on devices with limited memory

## Dependencies

### Core Dependencies

```kotlin
// build.gradle.kts (app module)
dependencies {
    // Android core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Hilt for dependency injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room for database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    // ThreeTenABP for time handling
    implementation(libs.threetenabp)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
```

### Version Catalog

```toml
# libs.versions.toml
[versions]
agp = "8.2.0"
kotlin = "1.9.0"
core-ktx = "1.12.0"
junit = "4.13.2"
androidx-test-ext-junit = "1.1.5"
espresso-core = "3.5.1"
lifecycle-runtime-ktx = "2.6.2"
activity-compose = "1.8.1"
compose-bom = "2023.10.01"
room = "2.6.0"
hilt = "2.48"
hilt-navigation-compose = "1.1.0"
threetenabp = "1.4.6"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "core-ktx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-test-ext-junit = { group = "androidx.test.ext", name = "junit", version.ref = "androidx-test-ext-junit" }
androidx-test-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espresso-core" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle-runtime-ktx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activity-compose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
androidx-hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version.ref = "hilt-navigation-compose" }
threetenabp = { group = "com.jakewharton.threetenabp", name = "threetenabp", version.ref = "threetenabp" }
```

## Tool Usage Patterns

### Time Updates

The app uses Android's `BroadcastReceiver` to listen for system time changes:

```kotlin
// Register receiver for time tick events (sent every minute)
registerReceiver(timeTickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

// BroadcastReceiver implementation
private val timeTickReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_TIME_TICK) {
            // Update time in ViewModel
            viewModel.updateCurrentTime()
        }
    }
}
```

### Database Access

Room database is accessed through repositories and exposed as Flows:

```kotlin
// Repository implementation
override fun getSelectedExchanges(): Flow<List<Exchange>> {
    return exchangeDao.getSelectedExchanges().map { entities ->
        entities.map { it.toDomainModel() }
    }
}

// ViewModel usage
getExchangesWithStatusUseCase().stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
).collect { exchanges ->
    _uiState.value = _uiState.value.copy(
        exchanges = exchanges,
        isLoading = false
    )
}
```

### UI State Management

The app uses StateFlow to manage UI state:

```kotlin
// ViewModel state definition
data class ExchangeListUiState(
    val exchanges: List<Exchange> = emptyList(),
    val currentLocalTime: LocalDateTime = LocalDateTime.now(),
    val isLoading: Boolean = true,
    val error: String? = null
)

// State collection in Composable
val uiState by viewModel.uiState.collectAsState()