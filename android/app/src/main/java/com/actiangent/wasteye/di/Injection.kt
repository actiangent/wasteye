package com.actiangent.wasteye.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.actiangent.wasteye.data.preferences.UserPreferencesSerializer
import com.actiangent.wasteye.data.preferences.WasteyePreferencesDataSource
import com.actiangent.wasteye.datastore.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class Injection(context: Context) {

    private val userPreferencesDataStore: DataStore<UserPreferences> =
        DataStoreFactory.create(
            serializer = UserPreferencesSerializer(),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        ) {
            context.dataStoreFile("user_preferences.pb")
        }

    val preferencesDataSource = WasteyePreferencesDataSource(userPreferencesDataStore)
}