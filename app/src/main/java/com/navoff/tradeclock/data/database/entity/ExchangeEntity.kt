package com.navoff.tradeclock.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.navoff.tradeclock.domain.models.Exchange
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId

/**
 * Room entity representing a stock exchange in the database.
 */
@Entity(tableName = "exchanges")
data class ExchangeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val timezoneName: String,
    val openingTimeHour: Int,
    val openingTimeMinute: Int,
    val closingTimeHour: Int,
    val closingTimeMinute: Int,
    val continent: String,
    val country: String,
    val city: String,
    val flag: String,
    val scheduleUrl: String = "",
    val isSelected: Boolean = true,
    val displayOrder: Int = 0, // Default order is 0, will be set during migration
    // Weekday operation flags - by default exchanges operate Monday-Friday
    val isOpenMonday: Boolean = true,
    val isOpenTuesday: Boolean = true,
    val isOpenWednesday: Boolean = true,
    val isOpenThursday: Boolean = true,
    val isOpenFriday: Boolean = true,
    val isOpenSaturday: Boolean = false,
    val isOpenSunday: Boolean = false
) {
    /**
     * Convert this database entity to a domain model.
     */
    fun toDomainModel(): Exchange {
        return Exchange(
            id = id,
            name = name,
            timezone = ZoneId.of(timezoneName),
            openingTime = LocalTime.of(openingTimeHour, openingTimeMinute),
            closingTime = LocalTime.of(closingTimeHour, closingTimeMinute),
            continent = continent,
            country = country,
            city = city,
            flag = flag,
            scheduleUrl = scheduleUrl,
            isSelected = isSelected,
            displayOrder = displayOrder,
            isOpenMonday = isOpenMonday,
            isOpenTuesday = isOpenTuesday,
            isOpenWednesday = isOpenWednesday,
            isOpenThursday = isOpenThursday,
            isOpenFriday = isOpenFriday,
            isOpenSaturday = isOpenSaturday,
            isOpenSunday = isOpenSunday
        )
    }

    companion object {
        /**
         * Convert a domain model to a database entity.
         */
        fun fromDomainModel(exchange: Exchange): ExchangeEntity {
            return ExchangeEntity(
                id = exchange.id,
                name = exchange.name,
                timezoneName = exchange.timezone.id,
                openingTimeHour = exchange.openingTime.hour,
                openingTimeMinute = exchange.openingTime.minute,
                closingTimeHour = exchange.closingTime.hour,
                closingTimeMinute = exchange.closingTime.minute,
                continent = exchange.continent,
                country = exchange.country,
                city = exchange.city,
                flag = exchange.flag,
                scheduleUrl = exchange.scheduleUrl,
                isSelected = exchange.isSelected,
                displayOrder = exchange.displayOrder,
                isOpenMonday = exchange.isOpenMonday,
                isOpenTuesday = exchange.isOpenTuesday,
                isOpenWednesday = exchange.isOpenWednesday,
                isOpenThursday = exchange.isOpenThursday,
                isOpenFriday = exchange.isOpenFriday,
                isOpenSaturday = exchange.isOpenSaturday,
                isOpenSunday = exchange.isOpenSunday
            )
        }
    }
}