package com.sarabyeet.toget.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sarabyeet.toget.BuildConfig
import com.sarabyeet.toget.util.UserColors.PreferencesKeys.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserColors(private val context: Context) {


    private object PreferencesKeys {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
            name = "${BuildConfig.APPLICATION_ID}.preferences"
        )
        val HIGH_PRIORITY_KEY = intPreferencesKey("high_pref_key")
        val MEDIUM_PRIORITY_KEY = intPreferencesKey("med_pref_key")
        val LOW_PRIORITY_KEY = intPreferencesKey("low_pref_key")

        const val DEFAULT_HIGH_COLOR = -1374457
        const val DEFAULT_MEDIUM_COLOR = -605950
        const val DEFAULT_LOW_COLOR = -13900237
    }

    suspend fun clear() {
        context.dataStore.edit {
            it.clear()
        }
    }

    fun getHighPriorityColor(): Flow<Int> = getInt(PreferencesKeys.HIGH_PRIORITY_KEY, PreferencesKeys.DEFAULT_HIGH_COLOR)
    fun getMediumPriorityColor(): Flow<Int> = getInt(PreferencesKeys.MEDIUM_PRIORITY_KEY, PreferencesKeys.DEFAULT_MEDIUM_COLOR)
    fun getLowPriorityColor(): Flow<Int> = getInt(PreferencesKeys.LOW_PRIORITY_KEY, PreferencesKeys.DEFAULT_LOW_COLOR)

    suspend fun setHighPriorityColor(color: Int) = setInt(PreferencesKeys.HIGH_PRIORITY_KEY, color)
    suspend fun setMediumPriorityColor(color: Int) =
        setInt(PreferencesKeys.MEDIUM_PRIORITY_KEY, color)

    suspend fun setLowPriorityColor(color: Int) = setInt(PreferencesKeys.LOW_PRIORITY_KEY, color)

    private suspend fun setInt(key: Preferences.Key<Int>, color: Int) {
        context.dataStore.edit {
            it[key] = color
        }
    }

    private fun getInt(key: Preferences.Key<Int>, defaultColor: Int): Flow<Int> = context.dataStore.data.map {
        it[key] ?: defaultColor
    }
}