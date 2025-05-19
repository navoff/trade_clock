# TradeClock Android

TradeClock is an Android application designed to help traders, investors, and financial professionals quickly check when various stock exchanges around the world open and close. The app displays the current local time of the user, along with a list of exchanges showing their local times, opening/closing hours, and current status (open/closed).

## Features

- **Real-time Exchange Status**: Instantly see which exchanges are currently open or closed
- **Global Exchange Coverage**: Track major stock exchanges from around the world
- **Local Time Display**: View the current local time at each exchange's location
- **Automatic Updates**: Time and status information updates every minute while the app is active
- **Customizable Exchange List**: Select which exchanges to display and reorder them according to your preference
- **Expandable Exchange Details**: Tap on an exchange to see more detailed information
- **Weekday Awareness**: Correctly handles weekday vs. weekend operating schedules
- **Visual Status Indicators**: Clear visual distinction between open and closed exchanges
- **Drag and Drop Reordering**: Easily reorder exchanges in edit mode with intuitive drag and drop

## Technologies Used

### Core Technologies

- **Kotlin**: Modern, concise language officially supported by Android
- **Jetpack Compose**: Declarative UI toolkit for building native Android UI
- **Android SDK**: Core Android platform functionality
- **Gradle**: Manages dependencies and build process

### Architecture Components

- **Clean Architecture**: Clear separation of concerns with domain, data, and presentation layers
- **MVVM Pattern**: Model-View-ViewModel architecture for the presentation layer
- **ViewModel**: Manages UI-related data in a lifecycle-conscious way
- **StateFlow**: Reactive state holder for UI state management
- **Room**: SQLite abstraction layer with compile-time verification
- **Hilt**: Dependency injection framework for Android

### Time Management

- **ThreeTenABP**: Android backport of java.time (JSR-310)
- **ZoneId**: Identifies time zone for each exchange
- **LocalTime**: Represents time without date or time zone
- **ZonedDateTime**: Represents date-time with time zone context

### Asynchronous Programming

- **Coroutines**: Kotlin's solution for asynchronous programming
- **Flow**: Reactive streams for asynchronous data
- **BroadcastReceiver**: Android component for receiving system broadcasts

## Project Structure

```text
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

## Setup and Installation

### Requirements

- Android Studio Arctic Fox (2021.3.1) or newer
- Minimum SDK: API 21 (Android 5.0)
- Target SDK: API 34 (Android 14)

### Building the Project

1. Clone the repository:
   ```
   git clone https://github.com/navoff/trade_clock.git
   ```
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the app on an emulator or physical device

## Current Status

The app is in a functional state with the following features implemented:

- Basic app structure with Clean Architecture and MVVM pattern
- Room database setup for storing exchange information
- Exchange list display with Jetpack Compose
- Current time display in the app header
- Local time display for each exchange
- Exchange status calculation (open/closed) based on time and day of week
- Weekday tracking (exchanges open Monday-Friday, closed on weekends)
- Visual distinction between open and closed exchanges
- Automatic time updates every minute when app is active
- Immediate time updates when app is resumed
- Exchange filtering functionality (showing/hiding based on selection)
- Edit mode with all exchanges available for selection
- Drag and drop reordering of exchanges in edit mode

## Roadmap

### Planned Features

- Detailed exchange information screen
- Search functionality
- Home screen widget
- Notifications for exchange opening/closing
- Backend integration for dynamic exchange data

## Contributing

Contributions are welcome! If you'd like to contribute to the project, please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Merge Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [ThreeTenABP](https://github.com/JakeWharton/ThreeTenABP) for time handling
- [Trading Hours](https://www.tradinghours.com) for exchange schedule information
