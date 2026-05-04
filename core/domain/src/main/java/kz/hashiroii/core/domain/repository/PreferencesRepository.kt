package kz.hashiroii.core.domain.repository

import kotlinx.coroutines.flow.Flow
import kz.hashiroii.core.domain.model.ThemePreference

interface PreferencesRepository {
    val themePreference: Flow<ThemePreference>
    suspend fun setThemePreference(theme: ThemePreference)
    fun getInitialThemeBlocking(): ThemePreference
}