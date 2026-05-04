package kz.hashiroii.feature.habit.add

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import kz.hashiroii.core.designsystem.theme.HabitHubTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HabitAddScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun top_bar_shows_add_habit_title() {
        composeRule.setContent {
            HabitHubTheme {
                HabitAddContent(uiState = HabitAddUiState(), onIntent = {})
            }
        }
        composeRule.onNodeWithText("Add Habit").assertIsDisplayed()
    }

    @Test
    fun name_error_shown_when_isNameError_true() {
        composeRule.setContent {
            HabitHubTheme {
                HabitAddContent(uiState = HabitAddUiState(isNameError = true), onIntent = {})
            }
        }
        composeRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
    }

    @Test
    fun save_button_is_visible() {
        composeRule.setContent {
            HabitHubTheme {
                HabitAddContent(uiState = HabitAddUiState(), onIntent = {})
            }
        }
        composeRule.onNodeWithText("Save Habit").assertIsDisplayed()
    }

    @Test
    fun save_click_triggers_SaveClicked_intent() {
        var intentFired = false
        composeRule.setContent {
            HabitHubTheme {
                HabitAddContent(
                    uiState = HabitAddUiState(),
                    onIntent = { if (it == HabitAddIntent.SaveClicked) intentFired = true },
                )
            }
        }
        composeRule.onNodeWithText("Save Habit").performClick()
        assert(intentFired)
    }

    @Test
    fun icon_section_label_is_visible() {
        composeRule.setContent {
            HabitHubTheme {
                HabitAddContent(uiState = HabitAddUiState(), onIntent = {})
            }
        }
        composeRule.onNodeWithText("Icon").assertIsDisplayed()
    }

    @Test
    fun color_section_label_is_visible() {
        composeRule.setContent {
            HabitHubTheme {
                HabitAddContent(uiState = HabitAddUiState(), onIntent = {})
            }
        }
        composeRule.onNodeWithText("Color").assertIsDisplayed()
    }

    @Test
    fun daily_repetitions_row_shows_once_per_day_for_goal_1() {
        composeRule.setContent {
            HabitHubTheme {
                HabitAddContent(uiState = HabitAddUiState(goalCount = 1), onIntent = {})
            }
        }
        composeRule.onNodeWithText("Once per day").assertIsDisplayed()
    }

    @Test
    fun daily_repetitions_row_shows_count_for_goal_above_1() {
        composeRule.setContent {
            HabitHubTheme {
                HabitAddContent(uiState = HabitAddUiState(goalCount = 3), onIntent = {})
            }
        }
        composeRule.onNodeWithText("3 times per day").assertIsDisplayed()
    }
}