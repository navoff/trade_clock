# System Patterns: TradeClock Android App

## System Architecture

TradeClock follows the **Clean Architecture** pattern with **MVVM** (Model-View-ViewModel) for the presentation layer. This architecture provides clear separation of concerns, testability, and maintainability.

### Architecture Layers

```
┌─────────────────────────────────────────────────┐
│                                                 │
│  Presentation Layer                             │
│  ┌─────────────────┐  ┌─────────────────────┐  │
│  │                 │  │                     │  │
│  │   Composables   │──▶     ViewModels      │  │
│  │                 │  │                     │  │
│  └─────────────────┘  └─────────────────────┘  │
│                              │                  │
└──────────────────────────────┼──────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────┐
│                                                 │
│  Domain Layer                                   │
│  ┌─────────────────┐  ┌─────────────────────┐  │
│  │                 │  │                     │  │
│  │    Use Cases    │──▶   Domain Models     │  │
│  │                 │  │                     │  │
│  └─────────────────┘  └─────────────────────┘  │
│           │                                     │
│           │          ┌─────────────────────┐   │
│           │          │                     │   │
│           └─────────▶│ Repository Interfaces│   │
│                      │                     │   │
│                      └─────────────────────┘   │
│                              │                  │
└──────────────────────────────┼──────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────┐
│                                                 │
│  Data Layer                                     │
│  ┌─────────────────┐                           │
│  │                 │                           │
│  │   Repository    │                           │
│  │ Implementations │                           │
│  │                 │                           │
│  └─────────────────┘                           │
│           │                                     │
│           ▼                                     │
│  ┌─────────────────┐                           │
│  │                 │                           │
│  │  Room Database  │                           │
│  │                 │                           │
│  └─────────────────┘                           │
│                                                 │
└─────────────────────────────────────────────────┘
```

## Key Technical Decisions

1. **Jetpack Compose** for UI: Modern declarative UI toolkit that simplifies UI development
2. **Room Database** for local storage: Provides type-safe access to SQLite with Kotlin coroutines support
3. **Hilt** for dependency injection: Simplifies DI setup compared to manual Dagger implementation
4. **Kotlin Coroutines & Flow** for asynchronous operations: Provides reactive programming model
5. **ThreeTenABP** for time handling: Robust library for working with time zones and time calculations
6. **BroadcastReceiver** for time updates: Efficient way to receive system time change notifications

## Design Patterns in Use

1. **Repository Pattern**: Abstracts data sources and provides a clean API to the domain layer
2. **Factory Pattern**: Used for creating database instances and other complex objects
3. **Observer Pattern**: Implemented via Kotlin Flow for reactive data updates
4. **Dependency Injection**: Used throughout the app for loose coupling and testability
5. **Builder Pattern**: Used in composable functions for UI construction
6. **State Hoisting**: Lifting state up in the composable hierarchy for better state management

## Component Relationships

### Data Flow

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│              │    │              │    │              │
│  Composables │◀──▶│  ViewModels  │◀──▶│  Use Cases   │
│              │    │              │    │              │
└──────────────┘    └──────────────┘    └──────────────┘
                                              │
                                              │
                                              ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│              │    │              │    │              │
│ Room Database│◀──▶│ Repositories │◀──▶│ Domain Models│
│              │    │              │    │              │
└──────────────┘    └──────────────┘    └──────────────┘
```

### Time Update Mechanism

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│              │    │              │    │              │
│ System Clock │───▶│BroadcastRcvr │───▶│  ViewModel   │
│              │    │              │    │              │
└──────────────┘    └──────────────┘    └──────────────┘
                                              │
                                              │
                                              ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│              │    │              │    │              │
│   UI State   │◀───│ Load Exchanges│◀──▶│ Repositories │
│              │    │              │    │              │
└──────────────┘    └──────────────┘    └──────────────┘
```

## Critical Implementation Paths

1. **Time Update Flow**:
   - System broadcasts time change (ACTION_TIME_TICK)
   - MainActivity's BroadcastReceiver captures the event
   - ViewModel's updateCurrentTime() is called
   - UI state is updated with new time
   - Exchanges are reloaded to update their status
   - UI recomposes with new data

2. **Exchange Status Calculation**:
   - Current time is obtained for the exchange's time zone
   - Current day of week is determined for the exchange's time zone
   - Day of week is checked against exchange's operating days (Monday-Friday)
   - If it's an operating day, opening and closing times are compared with current time
   - Status is determined (open/closed) based on both day of week and time of day
   - UI reflects the status with appropriate styling

3. **App Lifecycle Management**:
   - onResume: Register BroadcastReceiver, update time immediately
   - onPause: Unregister BroadcastReceiver to prevent unnecessary updates

4. **Data Initialization**:
   - Database is pre-populated with exchange data
   - Repository provides this data to the domain layer
   - Use cases apply business logic (status calculation)
   - ViewModels expose the processed data to the UI