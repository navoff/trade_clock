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
    val isSelected: Boolean = true
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
            isSelected = isSelected
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
                isSelected = exchange.isSelected
            )
        }
    }
}