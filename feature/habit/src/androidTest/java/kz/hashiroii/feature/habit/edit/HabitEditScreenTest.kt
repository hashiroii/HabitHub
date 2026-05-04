package kz.hashiroii.feature.habit.edit

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import kz.hashiroii.core.designsystem.theme.HabitHubTheme
import kz.hashiroii.core.domain.model.DayActivity
import kz.hashiroii.core.domain.model.Habit
import kz.hashiroii.core.domain.model.HabitWithStreak
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitEditScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun sampleState(name: String = "Morning Run", streak: Int = 5) =
        HabitEditUiState.Success(
            habitWithStreak = HabitWithStreak(
                habit = Habit(1L, name, colorHex = "#4CAF50", goalCount = 1),
                streakDays = streak,
                todayCompletionCount = 0,
                activityGrid = List(364) { i -> DayActivity(i.toLong(), 0, 1) },
            ),
            completionsByDay = emptyMap(),
        )

    @Test
    fun loading_state_shows_progress_indicator() {
        composeRule.setContent {
            HabitHubTheme {
                HabitEditContent(uiState = HabitEditUiState.Loading, onIntent = {})
            }
        }
        // Progress indicator has no text, just ensure the screen doesn't crash
        composeRule.waitForIdle()
    }

    @Test
    fun error_state_shows_error_message() {
        composeRule.setContent {
            HabitHubTheme {
                HabitEditContent(uiState = HabitEditUiState.Error("Load failed"), onIntent = {})
            }
        }
        composeRule.onNodeWithText("Load failed").assertIsDisplayed()
    }

    @Test
    fun success_state_shows_habit_name_in_top_bar() {
        composeRule.setContent {
            HabitHubTheme {
                HabitEditContent(uiState = sampleState("Yoga"), onIntent = {})
            }
        }
        composeRule.onNodeWithText("Yoga").assertIsDisplayed()
    }

    @Test
    fun success_state_shows_streak_row() {
        composeRule.setContent {
            HabitHubTheme {
                HabitEditContent(uiState = sampleState(streak = 7), onIntent = {})
            }
        }
        composeRule.onNodeWithText("7 day streak").assertIsDisplayed()
    }

    @Test
    fun delete_confirm_dialog_shown_when_isDeleteConfirmVisible() {
        composeRule.setContent {
            HabitHubTheme {
                HabitEditContent(
                    uiState = sampleState("Run").copy(isDeleteConfirmVisible = true),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText("Delete habit").assertIsDisplayed()
    }

    @Test
    fun discard_warning_dialog_shown_when_isDiscardWarningVisible() {
        composeRule.setContent {
            HabitHubTheme {
                HabitEditContent(
                    uiState = sampleState().copy(isDiscardWarningVisible = true),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText("Unsaved changes").assertIsDisplayed()
    }

    @Test
    fun zero_streak_still_renders_correctly() {
        composeRule.setContent {
            HabitHubTheme {
                HabitEditContent(uiState = sampleState(streak = 0), onIntent = {})
            }
        }
        composeRule.onNodeWithText("0 day streak").assertIsDisplayed()
    }
}