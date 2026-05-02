package kz.hashiroii.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NaturePeople
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

object HabitIcons {
    val all: List<Pair<String, ImageVector>> = listOf(
        "DirectionsRun" to Icons.Default.DirectionsRun,
        "FitnessCenter" to Icons.Default.FitnessCenter,
        "SelfImprovement" to Icons.Default.SelfImprovement,
        "MenuBook" to Icons.Default.MenuBook,
        "Create" to Icons.Default.Create,
        "School" to Icons.Default.School,
        "Bedtime" to Icons.Default.Bedtime,
        "WaterDrop" to Icons.Default.WaterDrop,
        "Restaurant" to Icons.Default.Restaurant,
        "MusicNote" to Icons.Default.MusicNote,
        "Palette" to Icons.Default.Palette,
        "Code" to Icons.Default.Code,
        "Favorite" to Icons.Default.Favorite,
        "Star" to Icons.Default.Star,
        "EmojiEvents" to Icons.Default.EmojiEvents,
        "LocalFireDepartment" to Icons.Default.LocalFireDepartment,
        "Bolt" to Icons.Default.Bolt,
        "Psychology" to Icons.Default.Psychology,
        "NaturePeople" to Icons.Default.NaturePeople,
        "Spa" to Icons.Default.Spa,
        "FlightTakeoff" to Icons.Default.FlightTakeoff,
    )

    fun getIcon(name: String): ImageVector =
        all.firstOrNull { it.first == name }?.second ?: Icons.Default.Star
}

val habitColors = listOf(
    "#F44336", "#E91E63", "#9C27B0",
    "#673AB7", "#3F51B5", "#2196F3",
    "#03A9F4", "#00BCD4", "#009688",
    "#4CAF50", "#8BC34A", "#CDDC39",
    "#FFEB3B", "#FFC107", "#FF9800",
    "#FF5722", "#795548", "#9E9E9E",
    "#607D8B", "#FF4081", "#7C4DFF",
)