package kz.hashiroii.feature.home

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kz.hashiroii.core.domain.model.ThemePreference
import kz.hashiroii.core.domain.repository.PreferencesRepository

class FakePreferencesRepository : PreferencesRepository {
    private val _themePreference = MutableStateFlow(ThemePreference.SYSTEM)
    override val themePreference: Flow<ThemePreference> = _themePreference

    override suspend fun setThemePreference(theme: ThemePreference) {
        _themePreference.value = theme
    }

    override fun getInitialThemeBlocking(): ThemePreference = _themePreference.value
}