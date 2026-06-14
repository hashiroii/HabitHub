package kz.hashiroii.habithub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kz.hashiroii.core.domain.model.ThemePreference
import kz.hashiroii.core.domain.repository.PreferencesRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val themePreference: StateFlow<ThemePreference?> = preferencesRepository.themePreference
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null,
        )
}