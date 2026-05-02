package kz.hashiroii.feature.home

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.hashiroii.core.domain.model.ThemePreference
import kz.hashiroii.core.domain.repository.PreferencesRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
) : ViewModel() {

    val themePreference: StateFlow<ThemePreference> = preferencesRepository.themePreference
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemePreference.SYSTEM)

    fun setTheme(theme: ThemePreference) {
        viewModelScope.launch { preferencesRepository.setThemePreference(theme) }
    }

    fun setLanguage(tag: String?) {
        val localeList = if (tag == null) LocaleListCompat.getEmptyLocaleList()
                         else LocaleListCompat.forLanguageTags(tag)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}