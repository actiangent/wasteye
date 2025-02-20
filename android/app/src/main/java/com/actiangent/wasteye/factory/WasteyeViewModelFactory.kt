package com.actiangent.wasteye.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.actiangent.wasteye.di.Injection
import com.actiangent.wasteye.fragment.SettingsViewModel

class WasteyeViewModelFactory(private val injection: Injection) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> SettingsViewModel(
                injection.preferencesDataSource,
            )

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        } as T
    }
}