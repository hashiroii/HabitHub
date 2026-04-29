package kz.hashiroii.feature.habit.add

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kz.hashiroii.core.designsystem.theme.HabitHubTheme
import kz.hashiroii.feature.habit.HabitIcons
import kz.hashiroii.feature.habit.habitColors

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

    HabitAddContent(uiState = uiState, onIntent = viewModel::onIntent)
}

@Composable
internal fun HabitAddContent(
    uiState: HabitAddUiState,
    onIntent: (HabitAddIntent) -> Unit,
) {
    val selectedColor = rememberHabitColor(uiState.selectedColorHex)

    Scaffold { paddingValues ->
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
                    label = { Text("Name") },
                    singleLine = true,
                    isError = uiState.isNameError,
                    supportingText = if (uiState.isNameError) {
                        { Text("Name cannot be empty") }
                    } else null,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { onIntent(HabitAddIntent.DescriptionChanged(it)) },
                    label = { Text("Description (optional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Icon", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                IconPickerGrid(
                    selectedIconName = uiState.selectedIconName,
                    tintColor = selectedColor,
                    onIconSelected = { onIntent(HabitAddIntent.IconSelected(it)) },
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text("Color", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                ColorPickerGrid(
                    selectedColorHex = uiState.selectedColorHex,
                    onColorSelected = { onIntent(HabitAddIntent.ColorSelected(it)) },
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onIntent(HabitAddIntent.SaveClicked) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Save Habit")
                }

                Spacer(modifier = Modifier.height(16.dp))
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
    androidx.compose.runtime.remember(hex) {
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