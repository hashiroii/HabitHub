package kz.hashiroii.feature.habit.add

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import kz.hashiroii.core.designsystem.theme.HabitHubTheme
import kz.hashiroii.core.ui.HabitIcons
import kz.hashiroii.core.ui.habitColors
import kz.hashiroii.feature.habit.R

@Composable
fun HabitAddScreen(
    onClose: () -> Unit,
    viewModel: HabitAddViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                HabitAddEvent.Saved -> onClose()
            }
        }
    }

    HabitAddContent(uiState = uiState, onIntent = viewModel::onIntent, onClose = onClose)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HabitAddContent(
    uiState: HabitAddUiState,
    onIntent: (HabitAddIntent) -> Unit,
    onClose: () -> Unit = {},
) {
    val selectedColor = rememberHabitColor(uiState.selectedColorHex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.habit_add_title)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.habit_back))
                    }
                },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            IconGridBackground(
                selectedIconName = uiState.selectedIconName,
                tintColor = selectedColor,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Icon(
                    imageVector = HabitIcons.getIcon(uiState.selectedIconName),
                    contentDescription = null,
                    tint = selectedColor,
                    modifier = Modifier.size(80.dp),
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { onIntent(HabitAddIntent.NameChanged(it)) },
                    label = { Text(stringResource(R.string.habit_name_label)) },
                    singleLine = true,
                    isError = uiState.isNameError,
                    supportingText = if (uiState.isNameError) {
                        { Text(stringResource(R.string.habit_name_error)) }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { onIntent(HabitAddIntent.DescriptionChanged(it)) },
                    label = { Text(stringResource(R.string.habit_description_label)) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(stringResource(R.string.habit_icon_label), style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                IconPickerGrid(
                    selectedIconName = uiState.selectedIconName,
                    tintColor = selectedColor,
                    onIconSelected = { onIntent(HabitAddIntent.IconSelected(it)) },
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(stringResource(R.string.habit_color_label), style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                ColorPickerGrid(
                    selectedColorHex = uiState.selectedColorHex,
                    onColorSelected = { onIntent(HabitAddIntent.ColorSelected(it)) },
                )

                Spacer(modifier = Modifier.height(24.dp))

                RepetitionRow(
                    goalCount = uiState.goalCount,
                    onClick = { onIntent(HabitAddIntent.OpenRepetitionDialog) },
                )

                Spacer(modifier = Modifier.height(6.dp))

                RepetitionWarning()

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onIntent(HabitAddIntent.SaveClicked) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.habit_save))
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (uiState.isRepetitionDialogVisible) {
        RepetitionDialog(
            goalCount = uiState.goalCount,
            habitColor = selectedColor,
            onIncrease = { onIntent(HabitAddIntent.RepetitionCountChanged(uiState.goalCount + 1)) },
            onDecrease = { onIntent(HabitAddIntent.RepetitionCountChanged(uiState.goalCount - 1)) },
            onConfirm = { onIntent(HabitAddIntent.ConfirmRepetition) },
            onDismiss = { onIntent(HabitAddIntent.DismissRepetitionDialog) },
        )
    }
}

@Composable
private fun RepetitionRow(
    goalCount: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.habit_daily_repetitions),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = if (goalCount == 1) stringResource(R.string.habit_repetitions_once)
                       else stringResource(R.string.habit_repetitions_many, goalCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
        Text(
            text = goalCount.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun RepetitionWarning() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
            modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.habit_repetitions_warning),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
        )
    }
}

@Composable
private fun RepetitionDialog(
    goalCount: Int,
    habitColor: Color,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.habit_daily_repetitions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Grid preview: goalCount+1 cells from saturation 0 to 1
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val totalCells = goalCount + 1
                    repeat(totalCells) { index ->
                        val saturation = index.toFloat() / goalCount.coerceAtLeast(1)
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(lerp(emptyColor, habitColor, saturation)),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (goalCount == 1) stringResource(R.string.habit_repetitions_once)
                           else stringResource(R.string.habit_repetitions_many, goalCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    TextButton(
                        onClick = onDecrease,
                        enabled = goalCount > 1,
                    ) {
                        Text(
                            text = "−",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                    Text(
                        text = goalCount.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                    )
                    TextButton(onClick = onIncrease) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.habit_confirm))
                }
            }
        }
    }
}

@Composable
private fun IconGridBackground(
    selectedIconName: String,
    tintColor: Color,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(8.dp),
        userScrollEnabled = false,
        modifier = Modifier.fillMaxSize(),
    ) {
        items(HabitIcons.all) { (name, icon) ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(4.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (name == selectedIconName) tintColor.copy(alpha = 0.18f)
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun IconPickerGrid(
    selectedIconName: String,
    tintColor: Color,
    onIconSelected: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(140.dp),
    ) {
        items(HabitIcons.all) { (name, icon) ->
            val isSelected = name == selectedIconName
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) tintColor.copy(alpha = 0.15f)
                        else Color.Transparent,
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 0.dp,
                        color = if (isSelected) tintColor else Color.Transparent,
                        shape = CircleShape,
                    )
                    .clickable { onIconSelected(name) }
                    .padding(6.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = if (isSelected) tintColor
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun ColorPickerGrid(
    selectedColorHex: String,
    onColorSelected: (String) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.height(100.dp),
    ) {
        items(habitColors) { hex ->
            val color = rememberHabitColor(hex)
            val isSelected = hex == selectedColorHex
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = CircleShape,
                    )
                    .clickable { onColorSelected(hex) },
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberHabitColor(hex: String): Color =
    remember(hex) {
        runCatching { Color(AndroidColor.parseColor(hex)) }.getOrDefault(Color(0xFF4CAF50))
    }

@Preview(name = "Add · Light", showBackground = true)
@Composable
private fun HabitAddLightPreview() {
    HabitHubTheme(darkTheme = false) {
        HabitAddContent(
            uiState = HabitAddUiState(name = "Morning Run", selectedColorHex = "#4CAF50"),
            onIntent = {},
        )
    }
}

@Preview(name = "Add · Dark", showBackground = true)
@Composable
private fun HabitAddDarkPreview() {
    HabitHubTheme(darkTheme = true) {
        HabitAddContent(
            uiState = HabitAddUiState(name = "Meditate", selectedIconName = "SelfImprovement", selectedColorHex = "#9C27B0"),
            onIntent = {},
        )
    }
}

@Preview(name = "Repetition Dialog · goalCount=1", showBackground = true)
@Composable
private fun RepetitionDialogPreview1() {
    HabitHubTheme(darkTheme = false) {
        RepetitionDialog(
            goalCount = 1,
            habitColor = Color(0xFF4CAF50),
            onIncrease = {},
            onDecrease = {},
            onConfirm = {},
            onDismiss = {},
        )
    }
}

@Preview(name = "Repetition Dialog · goalCount=4", showBackground = true)
@Composable
private fun RepetitionDialogPreview4() {
    HabitHubTheme(darkTheme = false) {
        RepetitionDialog(
            goalCount = 4,
            habitColor = Color(0xFF2196F3),
            onIncrease = {},
            onDecrease = {},
            onConfirm = {},
            onDismiss = {},
        )
    }
}