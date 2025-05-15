# Active Context: TradeClock Android App

## Current Work Focus

The current focus is on:

1. Implementing automatic time updates for the TradeClock app:
   - Updating the current time in the header every minute when the app is active
   - Updating the local time for each exchange every minute when the app is active
   - Updating the exchange status (open/closed) based on the current time
   - Ensuring all time-related information updates immediately when the app is resumed

2. Improving the UI:
   - Replacing text status indicators with colored circle Unicode symbols (🟢/🔴)
   - Positioning status indicators before exchange names
   - Moving local time display to the right side of the row
   - Reformatting exchange hours from "Opens: 8:00 Closes: 16:30" to "8:00 - 16:30"
   - Replacing "Continent: Europe" with country flag, country, and city (e.g., "🇬🇧 UK, London")
   - Improving layout with better alignment and spacing

3. Enhancing the database:
   - Adding country, city, and flag fields to the Exchange model
   - Creating a database migration to update the schema
   - Populating the database with more detailed exchange information

These features are critical for the app's core functionality, as users need accurate, up-to-date information about exchange operating hours presented in a visually intuitive way.

## Recent Changes

### UI Improvements

We've improved the exchange status indicators:
1. Replaced text-based "OPEN"/"CLOSED" indicators with Unicode circle symbols
2. Used 🟢 (green circle) for open exchanges
3. Used 🔴 (red circle) for closed exchanges

This change makes the status more visually distinct and intuitive, allowing users to quickly scan and identify open exchanges.

### Time Update Implementation

We've implemented a system to update time information automatically:

1. Added a BroadcastReceiver in MainActivity to listen for system time changes (ACTION_TIME_TICK)
2. Made the updateCurrentTime() method in ExchangeListViewModel public
3. Modified the updateCurrentTime() method to also reload exchanges to update their status
4. Added proper lifecycle management to register/unregister the receiver

```kotlin
// In MainActivity.kt
private val timeTickReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_TIME_TICK) {
            // Update time in ViewModel when system time changes
            viewModel.updateCurrentTime()
        }
    }
}

override fun onResume() {
    super.onResume()

    // Register receiver for time tick events
    registerReceiver(timeTickReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

    // Update time immediately when app is resumed
    viewModel.updateCurrentTime()
}

override fun onPause() {
    super.onPause()

    // Unregister receiver when app is paused
    try {
        unregisterReceiver(timeTickReceiver)
    } catch (e: IllegalArgumentException) {
        // Receiver not registered
    }
}
```

```kotlin
// Enhanced Exchange model with time information
data class Exchange(
    val id: String,
    val name: String,
    val timezone: ZoneId,
    val openingTime: LocalTime,
    val closingTime: LocalTime,
    val continent: String,
    val country: String,
    val city: String,
    val flag: String,
    val isSelected: Boolean = true,
    // Current local time at the exchange's location
    val currentLocalTime: LocalTime = LocalTime.now(timezone),
    // Whether the exchange is currently open
    val isOpen: Boolean = false
)

// Improved time update method in ViewModel
fun updateCurrentTime() {
    viewModelScope.launch {
        // Update the current time in the UI state
        val currentTime = LocalDateTime.now()

        if (_uiState.value.exchanges.isEmpty()) {
            // If no exchanges are loaded yet, load them
            _uiState.value = _uiState.value.copy(
                currentLocalTime = currentTime
            )
            loadExchanges()
        } else {
            // Update the exchange list with current time information
            val updatedExchanges = _uiState.value.exchanges.map { exchange ->
                // Calculate current local time at the exchange's location
                val now = ZonedDateTime.now(exchange.timezone)
                val localTime = LocalTime.of(now.hour, now.minute)

                // Calculate if the exchange is open
                val isOpen = if (exchange.openingTime.isBefore(exchange.closingTime)) {
                    localTime.isAfter(exchange.openingTime) && localTime.isBefore(exchange.closingTime)
                } else {
                    localTime.isAfter(exchange.openingTime) || localTime.isBefore(exchange.closingTime)
                }

                // Create a new Exchange object with updated time information
                exchange.copy(
                    currentLocalTime = localTime,
                    isOpen = isOpen
                )
            }

            // Update the UI state with the new exchange list and current time
            _uiState.value = _uiState.value.copy(
                currentLocalTime = currentTime,
                exchanges = updatedExchanges
            )
        }
    }
}
```

## Next Steps

1. **Testing the Time Update Feature**:
   - Verify that the time updates correctly every minute
   - Confirm that the time updates immediately when the app is resumed
   - Test edge cases (e.g., app in background, device time changes)

2. **UI Refinements**:
   - Consider adding animations for status changes
   - Improve visual distinction between open and closed exchanges

3. **Performance Optimization**:
   - Profile the app to ensure time updates don't cause performance issues
   - Optimize the exchange status calculation if needed

4. **Additional Features**:
   - Implement exchange filtering functionality
   - Add sorting options (by opening time, continent)
   - Consider adding a dark theme

## Active Decisions and Considerations

### Time Update Approach

We chose to use Android's `BroadcastReceiver` with `ACTION_TIME_TICK` instead of a timer-based approach for several reasons:

1. **Efficiency**: The system broadcast is more battery-efficient than running our own timer
2. **Accuracy**: The broadcast is synchronized with the system clock
3. **Reliability**: We don't need to manage our own timer thread

### UI Update Optimization

We've optimized how time updates affect the UI:

1. **Pre-calculated Values**: Store time and status information directly in the Exchange model
2. **Efficient Updates**: Create new Exchange objects with updated time information without reloading from database
3. **Smooth Updates**: Prevent the entire list from "blinking" or "flickering" during time updates
4. **Real-time Updates**: Ensure local time for exchanges updates every minute

### UI State Management

We're using a single UI state object in the ViewModel to manage all UI-related data:

```kotlin
data class ExchangeListUiState(
    val exchanges: List<Exchange> = emptyList(),
    val currentLocalTime: LocalDateTime = LocalDateTime.now(),
    val isLoading: Boolean = true,
    val error: String? = null
)
```

This approach provides several benefits:
1. Single source of truth for the UI
2. Atomic updates to related UI elements
3. Easier testing and debugging

### Exchange Status Calculation

The exchange status is calculated in the ExchangeItem composable based on the current time:

```kotlin
val now = ZonedDateTime.now(exchange.timezone)
val currentTime = LocalTime.of(now.hour, now.minute)

val isOpen = if (exchange.openingTime.isBefore(exchange.closingTime)) {
    currentTime.isAfter(exchange.openingTime) && currentTime.isBefore(exchange.closingTime)
} else {
    currentTime.isAfter(exchange.openingTime) || currentTime.isBefore(exchange.closingTime)
}
```

This handles both standard cases (where opening time is before closing time) and overnight cases (where closing time is on the next day).

## Important Patterns and Preferences

1. **Reactive UI Updates**: Using Kotlin Flow and StateFlow for reactive UI updates
2. **Clean Architecture**: Maintaining separation between layers
3. **Immutable State**: Using immutable data classes for UI state
4. **Lifecycle Awareness**: Properly handling Android lifecycle events
5. **Declarative UI**: Using Jetpack Compose for a declarative UI approach

## Learnings and Project Insights

1. **System Broadcasts**: Using system broadcasts like ACTION_TIME_TICK is more efficient than custom timers for regular updates
2. **Time Zone Handling**: Working with time zones requires careful consideration of edge cases
3. **UI State Management**: A single state object simplifies UI updates and state management
4. **Lifecycle Management**: Proper lifecycle management is crucial for resource-efficient apps
5. **Compose Recomposition**: Jetpack Compose efficiently recomposes only the parts of the UI that need to change