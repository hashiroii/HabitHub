package kz.hashiroii.feature.reminders

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kz.hashiroii.core.designsystem.theme.HabitHubTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemindersScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun empty_state_shows_no_reminders_title() {
        composeRule.setContent {
            HabitHubTheme {
                RemindersContent(
                    uiState = RemindersUiState(habitName = "Morning Run"),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText("No reminders yet").assertIsDisplayed()
    }

    @Test
    fun empty_state_shows_add_hint() {
        composeRule.setContent {
            HabitHubTheme {
                RemindersContent(
                    uiState = RemindersUiState(habitName = "Morning Run"),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText("Tap + to add a daily reminder").assertIsDisplayed()
    }

    @Test
    fun reminder_list_shows_formatted_time() {
        composeRule.setContent {
            HabitHubTheme {
                RemindersContent(
                    uiState = RemindersUiState(
                        habitName = "Running",
                        reminders = listOf(ReminderTime(id = 1, hour = 7, minute = 30)),
                    ),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText("7:30 AM").assertIsDisplayed()
    }

    @Test
    fun reminder_list_shows_pm_time() {
        composeRule.setContent {
            HabitHubTheme {
                RemindersContent(
                    uiState = RemindersUiState(
                        habitName = "Running",
                        reminders = listOf(ReminderTime(id = 1, hour = 18, minute = 0)),
                    ),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText("6:00 PM").assertIsDisplayed()
    }

    @Test
    fun reminder_row_has_seven_day_toggle_circles() {
        composeRule.setContent {
            HabitHubTheme {
                RemindersContent(
                    uiState = RemindersUiState(
                        habitName = "Running",
                        reminders = listOf(
                            ReminderTime(id = 1, hour = 8, minute = 0, days = ReminderTime.ALL_DAYS),
                        ),
                    ),
                    onIntent = {},
                )
            }
        }
        // All 7 days are selected — delete button present
        composeRule.onAllNodesWithContentDescription("Delete reminder")[0].assertIsDisplayed()
    }

    @Test
    fun top_bar_shows_habit_name() {
        composeRule.setContent {
            HabitHubTheme {
                RemindersContent(
                    uiState = RemindersUiState(habitName = "Yoga"),
                    onIntent = {},
                )
            }
        }
        composeRule.onNodeWithText("Reminders · Yoga").assertIsDisplayed()
    }

    @Test
    fun delete_button_triggers_DeleteReminder_intent() {
        var deletedId: Int? = null
        composeRule.setContent {
            HabitHubTheme {
                RemindersContent(
                    uiState = RemindersUiState(
                        habitName = "Run",
                        reminders = listOf(ReminderTime(id = 42, hour = 6, minute = 0)),
                    ),
                    onIntent = { intent ->
                        if (intent is RemindersIntent.DeleteReminder) deletedId = intent.reminderId
                    },
                )
            }
        }
        composeRule.onAllNodesWithContentDescription("Delete reminder")[0].performClick()
        assert(deletedId == 42)
    }
}