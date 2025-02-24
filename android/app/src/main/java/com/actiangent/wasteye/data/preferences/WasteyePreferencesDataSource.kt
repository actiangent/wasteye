package com.actiangent.wasteye.data.preferences

import android.util.Log
import androidx.datastore.core.DataStore
import com.actiangent.wasteye.datastore.LanguagesProto
import com.actiangent.wasteye.datastore.UserPreferences
import com.actiangent.wasteye.model.Language
import com.actiangent.wasteye.model.UserData
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException

class WasteyePreferencesDataSource(
    private val userPreferences: DataStore<UserPreferences>,
) {

    val userData = userPreferences.data
        .map { userPreferences ->
            UserData(
                isShowDetectionScore = userPreferences.isShowDetectionScore,
                languagePreference = when (userPreferences.languagePreference) {
                    null,
                    LanguagesProto.UNRECOGNIZED,
                    LanguagesProto.ENGLISH -> Language.ENGLISH

                    LanguagesProto.BAHASA_INDONESIA -> Language.BAHASA_INDONESIA
                }
            )
        }
        .distinctUntilChanged()

    suspend fun setIsShowDetectionScore(show: Boolean) {
        try {
            userPreferences.updateData { preferences ->
                preferences.toBuilder()
                    .setIsShowDetectionScore(show)
                    .build()
            }
        } catch (ioException: IOException) {
            Log.e("WasteyePreferences", "Failed to update user preferences", ioException)
        }
    }

    suspend fun setLanguagePreference(language: Language) {
        try {
            userPreferences.updateData { preferences ->
                val selectedLanguageProto = when (language) {
                    Language.ENGLISH -> LanguagesProto.ENGLISH
                    Language.BAHASA_INDONESIA -> LanguagesProto.BAHASA_INDONESIA
                }
                preferences.toBuilder()
                    .setLanguagePreference(selectedLanguageProto)
                    .build()
            }
        } catch (ioException: IOException) {
            Log.e("WasteyePreferences", "Failed to update user preferences", ioException)
        }
    }
}