package kz.hashiroii.feature.home

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kz.hashiroii.core.domain.model.ThemePreference
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var fakePrefs: FakePreferencesRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setup() {
        fakePrefs = FakePreferencesRepository()
        viewModel = SettingsViewModel(fakePrefs)
    }

    @Test
    fun `initial theme is SYSTEM`() = runTest {
        viewModel.themePreference.test {
            assertEquals(ThemePreference.SYSTEM, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setTheme DARK updates themePreference`() = runTest {
        viewModel.themePreference.test {
            awaitItem() // SYSTEM
            viewModel.setTheme(ThemePreference.DARK)
            assertEquals(ThemePreference.DARK, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setTheme LIGHT updates themePreference`() = runTest {
        viewModel.themePreference.test {
            awaitItem() // SYSTEM
            viewModel.setTheme(ThemePreference.LIGHT)
            assertEquals(ThemePreference.LIGHT, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `setTheme back to SYSTEM resets preference`() = runTest {
        viewModel.setTheme(ThemePreference.DARK)
        viewModel.themePreference.test {
            awaitItem() // DARK
            viewModel.setTheme(ThemePreference.SYSTEM)
            assertEquals(ThemePreference.SYSTEM, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}