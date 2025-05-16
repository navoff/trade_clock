package com.navoff.tradeclock.domain.models

import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId

/**
 * Domain model representing a stock exchange.
 */
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
    val scheduleUrl: String = "",
    val isSelected: Boolean = true,
    // Display order for custom sorting
    val displayOrder: Int = 0,
    // Current local time at the exchange's location
    val currentLocalTime: LocalTime = LocalTime.now(timezone),
    // Whether the exchange is currently open
    val isOpen: Boolean = false,
    // Whether the exchange item is expanded in the UI
    val isExpanded: Boolean = false
)