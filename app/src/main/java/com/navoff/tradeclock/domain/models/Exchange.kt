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
    val isSelected: Boolean = true
)