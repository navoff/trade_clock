package com.navoff.tradeclock.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navoff.tradeclock.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the user agreement state.
 */
@HiltViewModel
class UserAgreementViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // UI state for the user agreement
    data class UserAgreementUiState(
        val isLoading: Boolean = true,
        val hasAcceptedAgreement: Boolean = false
    )

    private val _uiState = MutableStateFlow(UserAgreementUiState())
    val uiState: StateFlow<UserAgreementUiState> = _uiState.asStateFlow()

    init {
        // Check if the user has already accepted the agreement
        checkAgreementStatus()
    }

    /**
     * Check if the user has already accepted the agreement.
     */
    private fun checkAgreementStatus() {
        viewModelScope.launch {
            userPreferencesRepository.hasAcceptedAgreement.collect { hasAccepted ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    hasAcceptedAgreement = hasAccepted
                )
            }
        }
    }

    /**
     * Accept the user agreement.
     */
    fun acceptAgreement() {
        viewModelScope.launch {
            userPreferencesRepository.setHasAcceptedAgreement(true)
            // The UI state will be updated automatically via the Flow collection in checkAgreementStatus
        }
    }
}