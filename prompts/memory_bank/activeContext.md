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

4. Fixing exchange filtering functionality:
   - Ensuring exchanges with unchecked checkboxes disappear from the list after pressing Save
   - Showing all exchanges (not just selected ones) when in edit mode
   - Maintaining correct checkbox state for each exchange in edit mode

These features are critical for the app's core functionality, as users need accurate, up-to-date information about exchange operating hours presented in a visually intuitive way.

## Recent Changes

### Exchange Filtering Functionality Fix

We've fixed issues with the exchange filtering functionality:

1. Fixed the issue where exchanges with unchecked checkboxes weren't disappearing from the list after pressing Save
2. Implemented showing all exchanges (not just selected ones) when in edit mode
3. Ensured checkboxes correctly reflect the current selection state of each exchange

The implementation includes:

```kotlin
// In ExchangeListViewModel.kt

// Save changes made in edit mode and exit edit mode
fun saveChanges() {
    viewModelScope.launch {
        // Save the selection changes to the repository
        _uiState.value.exchanges.forEach { exchange ->
            exchangeRepository.updateExchangeSelection(exchange.id, exchange.isSelected)
        }

        // Exit edit mode
        _uiState.value = _uiState.value.copy(
            isEditMode = false
        )

        // Reload exchanges to reflect the changes
        loadExchanges()
    }
}

// Enter edit mode for the exchange list
fun enterEditMode() {
    viewModelScope.launch {
        // Load all exchanges, not just selected ones
        loadAllExchanges()

        // Set edit mode flag
        _uiState.value = _uiState.value.copy(
            isEditMode = true
        )
    }
}

// Load all exchanges from the repository, not just selected ones
private fun loadAllExchanges() {
    viewModelScope.launch {
        try {
            exchangeRepository.getAllExchanges().stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            ).collect { exchanges ->
                // Update exchanges with current time information
                val updatedExchanges = updateExchangesWithTimeInfo(exchanges)

                _uiState.value = _uiState.value.copy(
                    exchanges = updatedExchanges,
                    isLoading = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message
            )
        }
    }
}
```

This implementation ensures:
- After pressing Save, exchanges with unchecked checkboxes disappear from the list
- When entering edit mode, all exchanges are available for selection
- Checkboxes reflect the current selection state of each exchange

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

### Expandable Exchange Items

We've implemented expandable exchange items to provide more detailed information on demand:

1. Added ability to expand/collapse exchange items by clicking anywhere on the card
2. Moved status indicator (🟢/🔴) to the right of local time
3. Added an expand/collapse indicator (arrow) to the right of trading hours
4. Implemented smooth animation for expanding/collapsing
5. Added detailed status information in expanded view showing time until opening/closing
6. Added a "View Schedule" button in expanded view to access exchange schedule

The expandable items provide a clean, compact interface while still allowing users to access more detailed information when needed. The implementation includes:

```kotlin
// Added to Exchange model
data class Exchange(
    // ... existing fields
    val isExpanded: Boolean = false
)

// Added to ExchangeListViewModel
fun toggleExchangeExpanded(exchangeId: String) {
    viewModelScope.launch {
        val currentExchanges = _uiState.value.exchanges
        val updatedExchanges = currentExchanges.map { exchange ->
            if (exchange.id == exchangeId) {
                // Toggle the expanded state for the selected exchange
                exchange.copy(isExpanded = !exchange.isExpanded)
            } else {
                exchange
            }
        }

        _uiState.value = _uiState.value.copy(
            exchanges = updatedExchanges
        )
    }
}

// Time remaining calculation for expanded view
private fun calculateTimeRemaining(exchange: Exchange): String {
    val currentTime = exchange.currentLocalTime

    return if (exchange.isOpen) {
        // Calculate time until closing
        val minutesUntilClosing = calculateMinutesUntil(currentTime, exchange.closingTime, exchange.openingTime)
        formatMinutesToTimeString(minutesUntilClosing)
    } else {
        // Calculate time until opening
        val minutesUntilOpening = calculateMinutesUntil(currentTime, exchange.openingTime, exchange.closingTime)
        formatMinutesToTimeString(minutesUntilOpening)
    }
}
```

## Next Steps

1. **Testing the Exchange Filtering Functionality**:
   - Verify that exchanges with unchecked checkboxes disappear after pressing Save
   - Confirm that all exchanges are shown in edit mode
   - Ensure checkboxes correctly reflect the selection state

2. **Testing the Expandable Items Feature**:
   - Verify that expanding/collapsing works correctly on all devices
   - Test the time remaining calculation for accuracy
   - Ensure animations are smooth and performant

3. **UI Refinements**:
   - Consider adding animations for status changes
   - Improve visual distinction between open and closed exchanges
   - Refine the expanded view layout for better readability

4. **Performance Optimization**:
   - Profile the app to ensure time updates don't cause performance issues
   - Optimize the exchange status calculation if needed
   - Ensure efficient recomposition when items expand/collapse

5. **Additional Features**:
   - Implement exchange filtering functionality
   - Add sorting options (by opening time, continent)
   - Consider adding a dark theme
   - Add ability to customize which information is shown in expanded view

## Active Decisions and Considerations

### Exchange Filtering Implementation

We chose to implement the exchange filtering functionality with a clear separation between normal mode and edit mode:

1. **Normal Mode**: Only selected exchanges are shown
2. **Edit Mode**: All exchanges are shown with their correct selection state
3. **Save Changes**: Updates the database and returns to normal mode showing only selected exchanges
4. **Cancel Edit**: Discards changes and returns to normal mode showing only selected exchanges

This approach provides a clean user experience where users can:
- See only their selected exchanges in normal mode
- Edit the full list of exchanges when needed
- Have their changes properly reflected after saving

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
    val error: String? = null,
    val isEditMode: Boolean = false
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
6. **Progressive Disclosure**: Using expandable items to show additional information only when needed
7. **Visual Hierarchy**: Using alignment and spacing to create clear visual relationships between elements
8. **Consistent Spacing**: Maintaining consistent spacing between related elements (e.g., 8.dp between time and status)

## Learnings and Project Insights

1. **System Broadcasts**: Using system broadcasts like ACTION_TIME_TICK is more efficient than custom timers for regular updates
2. **Time Zone Handling**: Working with time zones requires careful consideration of edge cases
3. **UI State Management**: A single state object simplifies UI updates and state management
4. **Lifecycle Management**: Proper lifecycle management is crucial for resource-efficient apps
5. **Compose Recomposition**: Jetpack Compose efficiently recomposes only the parts of the UI that need to change
6. **Animation Benefits**: Animations provide visual cues that help users understand state changes
7. **Progressive Disclosure**: Hiding detailed information until needed improves initial comprehension
8. **Visual Alignment**: Careful alignment of UI elements creates a more professional and intuitive interface
9. **Time Calculation Complexity**: Calculating time remaining requires handling various edge cases like overnight periods
10. **Edit Mode Patterns**: Separating view mode from edit mode provides a cleaner user experience