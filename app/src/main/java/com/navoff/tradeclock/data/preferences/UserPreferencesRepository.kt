package com.navoff.tradeclock.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property for Context to create a single DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repository for managing user preferences.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Preference keys
    private object PreferencesKeys {
        val HAS_ACCEPTED_AGREEMENT = booleanPreferencesKey("has_accepted_agreement")
    }

    /**
     * Get whether the user has accepted the agreement.
     */
    val hasAcceptedAgreement: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HAS_ACCEPTED_AGREEMENT] ?: false
        }

    /**
     * Set whether the user has accepted the agreement.
     */
    suspend fun setHasAcceptedAgreement(hasAccepted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_ACCEPTED_AGREEMENT] = hasAccepted
        }
    }
}