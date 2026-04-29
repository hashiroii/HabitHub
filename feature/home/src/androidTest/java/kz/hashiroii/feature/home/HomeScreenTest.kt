package kz.hashiroii.feature.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
class HomeScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loading_state_shows_progress_indicator() {
        composeRule.setContent {
            HabitHubTheme {
                HomeContent(uiState = HomeUiState.Loading, onIntent = {})
            }
        }
        composeRule.onNodeWithTag(TAG_HOME_LOADING).assertIsDisplayed()
    }

    @Test
    fun error_state_shows_error_container_and_message() {
        composeRule.setContent {
            HabitHubTheme {
                HomeContent(uiState = HomeUiState.Error("Something went wrong"), onIntent = {})
            }
        }
        composeRule.onNodeWithTag(TAG_HOME_ERROR).assertIsDisplayed()
        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun success_state_shows_habit_names() {
        composeRule.setContent {
            HabitHubTheme {
                HomeContent(
                    uiState = HomeUiState.Success(
                        habits = listOf(
                            HabitWithStreak(
                                habit = Habit(1L, "Morning Run", colorHex = "#4CAF50", goalCount = 1),
                                streakDays = 3,
                                todayCompletionCount = 0,
                                activityGrid = List(364) { i -> DayActivity(i.toLong(), 0, 1) },
                            ),
                        ),
                        completedToday = 0,
                        totalHabits = 1,
                        overallStreakDays = 3,
                    ),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText("Morning Run").assertIsDisplayed()
    }

    @Test
    fun success_state_shows_completion_ratio_in_summary_card() {
        composeRule.setContent {
            HabitHubTheme {
                HomeContent(
                    uiState = HomeUiState.Success(
                        habits = emptyList(),
                        completedToday = 4,
                        totalHabits = 9,
                        overallStreakDays = 7,
                    ),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText("4/9").assertIsDisplayed()
    }
}