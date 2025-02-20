package com.actiangent.wasteye.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actiangent.wasteye.data.preferences.WasteyePreferencesDataSource
import com.actiangent.wasteye.model.Language
import com.actiangent.wasteye.model.UserData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val preferences: WasteyePreferencesDataSource) : ViewModel() {

    val userData: StateFlow<UserData> = preferences.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserData()
        )

    fun setShowDetectionScore(show: Boolean) {
        viewModelScope.launch {
            preferences.setShowDetectionScore(show)
        }
    }

    fun setLanguagePreference(language: Language) {
        viewModelScope.launch {
            preferences.setLanguagePreference(language)
        }
    }
}
