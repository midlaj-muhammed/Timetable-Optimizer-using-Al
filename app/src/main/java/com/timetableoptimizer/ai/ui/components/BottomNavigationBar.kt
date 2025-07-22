package com.timetableoptimizer.ai.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.timetableoptimizer.ai.R

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            route = "home",
            icon = Icons.Default.Home,
            label = stringResource(R.string.nav_home)
        ),
        BottomNavItem(
            route = "timetables",
            icon = Icons.Default.Schedule,
            label = stringResource(R.string.nav_timetables)
        ),
        BottomNavItem(
            route = "subjects",
            icon = Icons.Default.Book,
            label = stringResource(R.string.nav_subjects)
        ),
        BottomNavItem(
            route = "analytics",
            icon = Icons.Default.Analytics,
            label = stringResource(R.string.nav_analytics)
        ),
        BottomNavItem(
            route = "settings",
            icon = Icons.Default.Settings,
            label = stringResource(R.string.nav_settings)
        )
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(text = item.label)
                },
                selected = currentRoute == item.route,
                onClick = {
                    onNavigate(item.route)
                }
            )
        }
    }
}
