# Active Context: TradeClock Android App

## Current Work Focus

The current focus is on:

1. Implementing automatic time updates for the TradeClock app:
   - Updating the current time in the header every minute when the app is active
   - Updating the local time for each exchange every minute when the app is active
   - Updating the exchange status (open/closed) based on the current time
   - Ensuring all time-related information updates immediately when the app is resumed

2. Implementing automatic time updates for the TradeClock app:
   - Updating the current time in the header every minute when the app is active
   - Updating the local time for each exchange every minute when the app is active
   - Updating the exchange status (open/closed) based on the current time
   - Ensuring all time-related information updates immediately when the app is resumed

3. Improving the UI:
   - Replacing text status indicators with colored circle Unicode symbols (🟢/🔴)
   - Positioning status indicators before exchange names
   - Moving local time display to the right side of the row
   - Reformatting exchange hours from "Opens: 8:00 Closes: 16:30" to "8:00 - 16:30"
   - Replacing "Continent: Europe" with country flag, country, and city (e.g., "🇬🇧 UK, London")
   - Improving layout with better alignment and spacing

4. Enhancing the database:
   - Adding country, city, and flag fields to the Exchange model
   - Adding weekday information to track which days exchanges are open
   - Creating database migrations to update the schema
   - Populating the database with more detailed exchange information

5. Fixing exchange filtering functionality:
   - Ensuring exchanges with unchecked checkboxes disappear from the list after pressing Save
   - Showing all exchanges (not just selected ones) when in edit mode
   - Maintaining correct checkbox state for each exchange in edit mode

6. Implementing drag and drop functionality:
   - Adding ability to reorder exchanges in edit mode using drag and drop
   - Saving the custom order to the database
   - Providing haptic feedback during drag operations
   - Enhancing the UI with visual cues during dragging

These features are critical for the app's core functionality, as users need accurate, up-to-date information about exchange operating hours presented in a visually intuitive way.

## Recent Changes

### Database Enhancement: Added Weekday Tracking

We've added weekday tracking to the app to account for the fact that exchanges only operate on weekdays (Monday-Friday) and are closed on weekends (Saturday-Sunday):

1. Updated the database schema:
   - Added boolean fields for each day of the week (`isOpenMonday`, `isOpenTuesday`, etc.)
   - Created a migration from version 1 to 2 to add these new columns
   - Set default values (Monday-Friday = true, Saturday-Sunday = false)

2. Updated the Exchange model:
   - Added corresponding boolean fields in both the entity and domain models
   - Updated conversion methods between entity and domain models

3. Enhanced status calculation:
   - Modified `updateExchangesWithTimeInfo` in `ExchangeListViewModel` to check both:
     - If the current time is within opening/closing hours
     - If the exchange is open on the current day of the week
   - Exchange is only considered open if both conditions are met

4. Updated initial data:
   - Set all exchanges to operate Monday-Friday
   - Set all exchanges to be closed on Saturday and Sunday

This enhancement ensures that the app correctly shows exchanges as closed on weekends, even if the current time is within their normal operating hours.

### Database Enhancement: Added New Exchanges

We've expanded the database with additional stock exchanges:

1. Added Moscow Exchange (MOEX):
   - Located in Moscow, Russia (Europe)
   - Trading hours: 9:50 - 18:50 Moscow time
   - Added country, city, and flag (🇷🇺) information

2. Added Hong Kong Stock Exchange (HKEX):
   - Located in Hong Kong (Asia)
   - Trading hours: 9:30 - 16:00 Hong Kong time
   - Added country, city, and flag (🇭🇰) information

3. Added NASDAQ:
   - Located in New York, USA (North America)
   - Trading hours: 9:30 - 16:00 Eastern time
   - Added country, city, and flag (🇺🇸) information

4. Standardized schedule URLs:
   - Updated all exchange schedule URLs to use tradinghours.com for consistency
   - Example: https://www.tradinghours.com/markets/moex

These additions provide users with more comprehensive global market coverage and consistent access to trading hours information.

### Bug Fix: Exchange Order Not Saving

We've fixed the bug where the new order of exchanges wasn't being saved when the "Save" button was pressed:

1. Changed the order of operations in the saveChanges() method to save the display order first, then the selection changes
2. Added error handling with try-catch block to better handle and report errors
3. Ensured the display order is correctly saved to the database before exiting edit mode

The key issue was that the saveChanges() method wasn't saving the display order correctly. The fix ensures that:
- The display order is saved first, before any other operations
- Any errors during the save operation are properly caught and reported
- The UI state is updated correctly after saving

### Drag and Drop Reordering Implementation

We've implemented drag and drop functionality for reordering exchanges in edit mode:

1. Added long-press gesture to initiate dragging of exchange items
2. Implemented reordering logic to update the list when items are moved
3. Added haptic feedback for better user experience during dragging
4. Enhanced the UI with visual feedback during drag operations
5. Implemented saving of the custom order to the database

The implementation includes:

```kotlin
// In ExchangeListViewModel.kt

// Added to UI state to track dragging state
data class ExchangeListUiState(
    val exchanges: List<Exchange> = emptyList(),
    val currentLocalTime: LocalDateTime = LocalDateTime.now(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isEditMode: Boolean = false,
    val isDragging: Boolean = false
)

// Reorder exchanges by moving an exchange from one position to another
fun reorderExchanges(fromIndex: Int, toIndex: Int) {
    viewModelScope.launch {
        val currentExchanges = _uiState.value.exchanges.toMutableList()

        if (fromIndex < toIndex) {
            // Moving down
            for (i in fromIndex until toIndex) {
                val temp = currentExchanges[i]
                currentExchanges[i] = currentExchanges[i + 1]
                currentExchanges[i + 1] = temp
            }
        } else {
            // Moving up
            for (i in fromIndex downTo toIndex + 1) {
                val temp = currentExchanges[i]
                currentExchanges[i] = currentExchanges[i - 1]
                currentExchanges[i - 1] = temp
            }
        }

        // Update UI immediately
        _uiState.value = _uiState.value.copy(
            exchanges = currentExchanges
        )
    }
}

// Save the current display order of exchanges to the database
fun saveDisplayOrder() {
    viewModelScope.launch {
        val updates = _uiState.value.exchanges.mapIndexed { index, exchange ->
            exchange.id to index
        }.toMap()

        exchangeRepository.updateExchangeDisplayOrders(updates)
    }
}

// Set the dragging state
fun setDragging(isDragging: Boolean) {
    _uiState.value = _uiState.value.copy(
        isDragging = isDragging
    )
}

// Save changes made in edit mode and exit edit mode
fun saveChanges() {
    viewModelScope.launch {
        try {
            // Create a map of exchange IDs to their indices in the list
            val updates = _uiState.value.exchanges.mapIndexed { index, exchange ->
                exchange.id to index
            }.toMap()

            // Save the display order to the database
            exchangeRepository.updateExchangeDisplayOrders(updates)

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
        } catch (e: Exception) {
            // Log the error
            println("Error saving changes: ${e.message}")
            e.printStackTrace()

            // Update UI state with error
            _uiState.value = _uiState.value.copy(
                error = "Failed to save changes: ${e.message}"
            )
        }
    }
}
```

```kotlin
// In ExchangeListScreen.kt

// Use LazyColumn with ReorderableItem for drag and drop
val hapticFeedback = LocalHapticFeedback.current
val lazyListState = rememberLazyListState()
val reorderableLazyListState = rememberReorderableLazyListState(
    lazyListState = lazyListState,
    onMove = { from, to ->
        viewModel.reorderExchanges(from.index, to.index)
        // Provide haptic feedback when item is moved
        hapticFeedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
)

LazyColumn(
    state = lazyListState,
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(top = 8.dp)
) {
    itemsIndexed(
        items = uiState.exchanges,
        key = { _, exchange -> exchange.id }
    ) { index, exchange ->
        ReorderableItem(
            reorderableLazyListState = reorderableLazyListState,
            key = exchange.id
        ) { isDragging ->
            ExchangeItem(
                exchange = exchange,
                onToggleExpanded = { exchangeId ->
                    viewModel.toggleExchangeExpanded(exchangeId)
                },
                onToggleSelection = { exchangeId ->
                    viewModel.toggleExchangeSelection(exchangeId)
                },
                isEditMode = uiState.isEditMode,
                isDragging = isDragging,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .longPressDraggableHandle(
                        onDragStarted = {
                            viewModel.setDragging(true)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDragStopped = {
                            viewModel.setDragging(false)
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    )
            )
        }
    }
}
```

This implementation allows users to:
- Long-press on an exchange item to start dragging
- Move the item to a new position in the list
- Receive haptic feedback during the drag operation
- See visual feedback with elevated cards during dragging
- Have the custom order saved when pressing the Save button

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

1. **Testing the Drag and Drop Functionality**:
   - Verify that exchanges can be reordered in edit mode
   - Confirm that the order is saved correctly to the database
   - Test haptic feedback on different devices
   - Ensure the UI provides clear visual feedback during dragging

2. **Testing the Exchange Filtering Functionality**:
   - Verify that exchanges with unchecked checkboxes disappear after pressing Save
   - Confirm that all exchanges are shown in edit mode
   - Ensure checkboxes correctly reflect the selection state

3. **Testing the Expandable Items Feature**:
   - Verify that expanding/collapsing works correctly on all devices
   - Test the time remaining calculation for accuracy
   - Ensure animations are smooth and performant

4. **Performance Optimization**:
   - Profile the app to ensure time updates don't cause performance issues
   - Optimize the exchange status calculation if needed
   - Ensure efficient recomposition when items expand/collapse

5. **Additional Features**:
   - Implement detailed exchange information screen
   - Add search functionality
   - Develop home screen widget
   - Prepare for backend integration for dynamic exchange data

## Active Decisions and Considerations

### Drag and Drop Implementation

We chose to implement drag and drop functionality using the Reorderable library for several reasons:

1. **User Experience**: Long-press to drag is an intuitive gesture that users are familiar with
2. **Visual Feedback**: The library provides built-in support for visual feedback during dragging
3. **Haptic Feedback**: We added haptic feedback to enhance the physical feel of dragging
4. **Persistence**: The custom order is saved to the database for a consistent experience across sessions

The implementation follows these principles:
- **Immediate Visual Feedback**: The UI updates immediately when items are reordered
- **Persistent Storage**: The order is saved to the database when pressing the Save button
- **Tactile Feedback**: Haptic feedback is provided at key moments (start, during, end of drag)
- **Edit Mode Integration**: Drag and drop is only available in edit mode, consistent with other editing features

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
    val isEditMode: Boolean = false,
    val isDragging: Boolean = false
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
9. **Gesture-Based Interactions**: Using intuitive gestures like long-press for drag and drop
10. **Haptic Feedback**: Providing tactile feedback for important interactions

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
11. **Gesture Recognition**: Long-press gestures need proper visual and haptic feedback to be intuitive
12. **Order Persistence**: Saving user-defined order requires careful database design and update logic