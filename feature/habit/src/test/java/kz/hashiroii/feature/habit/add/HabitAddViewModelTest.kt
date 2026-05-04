package kz.hashiroii.feature.habit.add

import app.cash.turbine.test
import kotlin.test.assertIs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kz.hashiroii.feature.habit.FakeHabitRepository
import kz.hashiroii.feature.habit.MainCoroutineRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HabitAddViewModelTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var repo: FakeHabitRepository
    private lateinit var viewModel: HabitAddViewModel

    @Before
    fun setup() {
        repo = FakeHabitRepository()
        viewModel = HabitAddViewModel(repo)
    }

    @Test
    fun `initial state has empty fields and no errors`() {
        val state = viewModel.uiState.value
        assertEquals("", state.name)
        assertEquals("", state.description)
        assertEquals(1, state.goalCount)
        assertFalse(state.isNameError)
        assertFalse(state.isRepetitionDialogVisible)
    }

    @Test
    fun `NameChanged updates name and clears error flag`() = runTest {
        viewModel.onIntent(HabitAddIntent.SaveClicked)
        assertTrue(viewModel.uiState.value.isNameError)

        viewModel.onIntent(HabitAddIntent.NameChanged("Running"))

        assertEquals("Running", viewModel.uiState.value.name)
        assertFalse(viewModel.uiState.value.isNameError)
    }

    @Test
    fun `DescriptionChanged updates description`() {
        viewModel.onIntent(HabitAddIntent.DescriptionChanged("Every morning"))
        assertEquals("Every morning", viewModel.uiState.value.description)
    }

    @Test
    fun `SaveClicked with blank name sets isNameError`() {
        viewModel.onIntent(HabitAddIntent.NameChanged("   "))
        viewModel.onIntent(HabitAddIntent.SaveClicked)
        assertTrue(viewModel.uiState.value.isNameError)
    }

    @Test
    fun `SaveClicked with valid name emits Saved and persists habit`() = runTest {
        viewModel.onIntent(HabitAddIntent.NameChanged("Morning Run"))
        viewModel.events.test {
            viewModel.onIntent(HabitAddIntent.SaveClicked)
            assertIs<HabitAddEvent.Saved>(awaitItem())
        }
        val habits = repo.observeHabits().first()
        assertEquals(1, habits.size)
        assertEquals("Morning Run", habits.first().name)
    }

    @Test
    fun `ColorSelected updates selectedColorHex`() {
        viewModel.onIntent(HabitAddIntent.ColorSelected("#FF5722"))
        assertEquals("#FF5722", viewModel.uiState.value.selectedColorHex)
    }

    @Test
    fun `IconSelected updates selectedIconName`() {
        viewModel.onIntent(HabitAddIntent.IconSelected("Star"))
        assertEquals("Star", viewModel.uiState.value.selectedIconName)
    }

    @Test
    fun `RepetitionCountChanged enforces minimum of 1`() {
        viewModel.onIntent(HabitAddIntent.RepetitionCountChanged(0))
        assertEquals(1, viewModel.uiState.value.goalCount)

        viewModel.onIntent(HabitAddIntent.RepetitionCountChanged(-3))
        assertEquals(1, viewModel.uiState.value.goalCount)

        viewModel.onIntent(HabitAddIntent.RepetitionCountChanged(5))
        assertEquals(5, viewModel.uiState.value.goalCount)
    }

    @Test
    fun `OpenRepetitionDialog shows dialog`() {
        viewModel.onIntent(HabitAddIntent.OpenRepetitionDialog)
        assertTrue(viewModel.uiState.value.isRepetitionDialogVisible)
    }

    @Test
    fun `DismissRepetitionDialog hides dialog`() {
        viewModel.onIntent(HabitAddIntent.OpenRepetitionDialog)
        viewModel.onIntent(HabitAddIntent.DismissRepetitionDialog)
        assertFalse(viewModel.uiState.value.isRepetitionDialogVisible)
    }

    @Test
    fun `ConfirmRepetition hides dialog`() {
        viewModel.onIntent(HabitAddIntent.OpenRepetitionDialog)
        viewModel.onIntent(HabitAddIntent.ConfirmRepetition)
        assertFalse(viewModel.uiState.value.isRepetitionDialogVisible)
    }
}