package kz.hashiroii.feature.home

sealed interface HomeIntent {
    data class AddCompletion(val habitId: Long) : HomeIntent
    data class RemoveCompletion(val habitId: Long) : HomeIntent
}