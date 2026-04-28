package kz.hashiroii.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kz.hashiroii.core.designsystem.components.HabitHubCard
import kz.hashiroii.core.designsystem.components.HabitHubChip
import kz.hashiroii.core.designsystem.components.HabitHubIconButton
import kz.hashiroii.core.designsystem.components.HabitHubPrimaryButton
import kz.hashiroii.core.designsystem.components.HabitHubProgressIndicator
import kz.hashiroii.core.designsystem.theme.HabitHubTheme

private val GalleryBlue = Color(0xFF1565C0)
private val GalleryGreen = Color(0xFF2E7D32)

@Preview(name = "Style Guide · Light", showBackground = true, widthDp = 380)
@Composable
private fun GalleryLightPreview() {
    HabitHubTheme(darkTheme = false) {
        StyleGuideContent()
    }
}

@Preview(name = "Style Guide · Dark", showBackground = true, backgroundColor = 0xFF11140F, widthDp = 380)
@Composable
private fun GalleryDarkPreview() {
    HabitHubTheme(darkTheme = true) {
        StyleGuideContent()
    }
}

@Composable
private fun StyleGuideContent() {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = HabitHubTheme.spacing.large,
            vertical = HabitHubTheme.spacing.xLarge,
        ),
        verticalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.xLarge),
    ) {
        item { SectionHeader("Typography") }
        item { TypographySection() }

        item { SectionDivider() }
        item { SectionHeader("Buttons") }
        item { ButtonsSection() }

        item { SectionDivider() }
        item { SectionHeader("Chips") }
        item { ChipsSection() }

        item { SectionDivider() }
        item { SectionHeader("Progress Indicator") }
        item { ProgressSection() }

        item { SectionDivider() }
        item { SectionHeader("Card") }
        item { CardSection() }

        item { SectionDivider() }
        item { SectionHeader("Spacing Tokens") }
        item { SpacingSection() }

        item { SectionDivider() }
        item { SectionHeader("Shape Tokens") }
        item { ShapesSection() }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun StateLabel(label: String) {
    Text(
        text = label,
        style = HabitHubTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.width(64.dp),
    )
}

// ── Sections ──────────────────────────────────────────────────────────────────

@Composable
private fun TypographySection() {
    Column(verticalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.medium)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            StateLabel("habitTitle")
            Text(color = MaterialTheme.colorScheme.primary,text = "Morning Run", style = HabitHubTheme.typography.habitTitle)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            StateLabel("counter")
            Text(color = MaterialTheme.colorScheme.primary,text = "4 / 8", style = HabitHubTheme.typography.counterNumber)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            StateLabel("labelMedium")
            Text(color = MaterialTheme.colorScheme.primary,text = "Habit category", style = HabitHubTheme.typography.labelMedium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            StateLabel("labelSmall")
            Text(color = MaterialTheme.colorScheme.primary,text = "streak · 12 days", style = HabitHubTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun ButtonsSection() {
    val pressedSource = remember { MutableInteractionSource() }
    LaunchedEffect(pressedSource) {
        pressedSource.emit(PressInteraction.Press(Offset.Zero))
    }

    Column(verticalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.medium)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
        ) {
            StateLabel("normal")
            HabitHubPrimaryButton(text = "Add Habit", onClick = {})
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
        ) {
            StateLabel("disabled")
            HabitHubPrimaryButton(text = "Add Habit", onClick = {}, enabled = false)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
        ) {
            StateLabel("pressed")
            HabitHubPrimaryButton(
                text = "Add Habit",
                onClick = {},
                interactionSource = pressedSource,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
        ) {
            StateLabel("with icon")
            HabitHubPrimaryButton(
                text = "Start",
                onClick = {},
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DirectionsRun,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                },
            )
        }
        Spacer(modifier = Modifier.height(HabitHubTheme.spacing.extraSmall))
        Text("Icon Buttons", style = HabitHubTheme.typography.labelMedium)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
        ) {
            StateLabel("normal")
            HabitHubIconButton(onClick = {}) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
            HabitHubIconButton(onClick = {}) {
                Icon(Icons.Filled.Remove, contentDescription = null)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
        ) {
            StateLabel("disabled")
            HabitHubIconButton(onClick = {}, enabled = false) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    }
}

@Composable
private fun ChipsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.medium)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
        ) {
            StateLabel("states")
            HabitHubChip(label = "Running", selected = false, onClick = {})
            HabitHubChip(label = "Gym", selected = true, onClick = {})
            HabitHubChip(label = "Water", selected = false, onClick = {}, enabled = false)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
        ) {
            StateLabel("with icon")
            HabitHubChip(
                label = "Run",
                selected = true,
                onClick = {},
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                },
            )
        }
    }
}

@Composable
private fun ProgressSection() {
    val steps = listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
    Column(verticalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.medium)) {
        Text("Blue", style = HabitHubTheme.typography.labelMedium)
        steps.forEach { p ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
            ) {
                Text(
                    text = "${(p * 100).toInt()}%",
                    style = HabitHubTheme.typography.labelSmall,
                    modifier = Modifier.width(32.dp),
                )
                HabitHubProgressIndicator(
                    progress = p,
                    habitColor = GalleryBlue,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(modifier = Modifier.height(HabitHubTheme.spacing.extraSmall))
        Text("Green", style = HabitHubTheme.typography.labelMedium)
        steps.forEach { p ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default),
            ) {
                Text(
                    text = "${(p * 100).toInt()}%",
                    style = HabitHubTheme.typography.labelSmall,
                    modifier = Modifier.width(32.dp),
                )
                HabitHubProgressIndicator(
                    progress = p,
                    habitColor = GalleryGreen,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun CardSection() {
    HabitHubCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(HabitHubTheme.spacing.large)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.LocalDrink,
                    contentDescription = null,
                    tint = GalleryBlue,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(HabitHubTheme.spacing.medium))
                Text("Drink Water", style = HabitHubTheme.typography.habitTitle)
            }
            Spacer(modifier = Modifier.height(HabitHubTheme.spacing.medium))
            HabitHubProgressIndicator(
                progress = 0.625f,
                habitColor = GalleryBlue,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(HabitHubTheme.spacing.default))
            Text(
                text = "5 of 8 completed today",
                style = HabitHubTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SpacingSection() {
    val tokens = listOf(
        "extraSmall · 4dp" to HabitHubTheme.spacing.extraSmall,
        "default · 8dp" to HabitHubTheme.spacing.default,
        "medium · 12dp" to HabitHubTheme.spacing.medium,
        "large · 16dp" to HabitHubTheme.spacing.large,
        "xLarge · 24dp" to HabitHubTheme.spacing.xLarge,
        "xxLarge · 32dp" to HabitHubTheme.spacing.xxLarge,
    )
    Column(verticalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default)) {
        tokens.forEach { (label, size) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.medium),
            ) {
                Text(
                    text = label,
                    style = HabitHubTheme.typography.labelSmall,
                    modifier = Modifier.width(120.dp),
                )
                Box(
                    modifier = Modifier
                        .size(size)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
        }
    }
}

@Composable
private fun ShapesSection() {
    val tokens = listOf(
        "gridCell · 4dp" to HabitHubTheme.shapes.gridCell,
        "chip · 8dp" to HabitHubTheme.shapes.chip,
        "button · 12dp" to HabitHubTheme.shapes.button,
        "card · 20dp" to HabitHubTheme.shapes.card,
    )
    Column(verticalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.default)) {
        tokens.forEach { (label, radius) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(HabitHubTheme.spacing.medium),
            ) {
                Text(
                    text = label,
                    style = HabitHubTheme.typography.labelSmall,
                    modifier = Modifier.width(120.dp),
                )
                Surface(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(radius)),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    content = {},
                )
            }
        }
    }
}