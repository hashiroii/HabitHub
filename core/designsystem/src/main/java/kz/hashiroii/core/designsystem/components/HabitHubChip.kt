package kz.hashiroii.core.designsystem.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kz.hashiroii.core.designsystem.theme.HabitHubTheme

@Composable
fun HabitHubChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label, style = HabitHubTheme.typography.labelSmall) },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(HabitHubTheme.shapes.chip),
    )
}