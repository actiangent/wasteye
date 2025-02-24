package com.actiangent.wasteye.fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.actiangent.wasteye.data.preferences.WasteyePreferencesDataSource
import com.actiangent.wasteye.model.UserData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CameraViewModel(preferences: WasteyePreferencesDataSource) : ViewModel() {

    val userData: StateFlow<UserData> = preferences.userData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UserData()
        )
}