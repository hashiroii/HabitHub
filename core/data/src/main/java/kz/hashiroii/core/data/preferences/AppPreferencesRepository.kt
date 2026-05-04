package kz.hashiroii.core.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kz.hashiroii.core.domain.model.ThemePreference
import kz.hashiroii.core.domain.repository.PreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class AppPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) : PreferencesRepository {

    private val themeKey = stringPreferencesKey("theme_preference")

    override val themePreference: Flow<ThemePreference> = context.dataStore.data
        .map { prefs ->
            when (prefs[themeKey]) {
                ThemePreference.LIGHT.name -> ThemePreference.LIGHT
                ThemePreference.DARK.name -> ThemePreference.DARK
                else -> ThemePreference.SYSTEM
            }
        }

    override suspend fun setThemePreference(theme: ThemePreference) {
        context.dataStore.edit { prefs -> prefs[themeKey] = theme.name }
    }

    // Blocking read used only once at startup to avoid a first-frame flicker.
    override fun getInitialThemeBlocking(): ThemePreference = runBlocking { themePreference.first() }
}