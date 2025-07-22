package com.timetableoptimizer.ai.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.timetableoptimizer.ai.ui.components.BottomNavigationBar
import com.timetableoptimizer.ai.ui.screens.home.HomeScreen
import com.timetableoptimizer.ai.ui.screens.timetables.TimetablesScreen
import com.timetableoptimizer.ai.ui.screens.subjects.SubjectsScreen
import com.timetableoptimizer.ai.ui.screens.settings.SettingsScreen
import com.timetableoptimizer.ai.ui.screens.analytics.AnalyticsScreen
import com.timetableoptimizer.ai.ui.theme.TimetableOptimizerAITheme
import com.timetableoptimizer.ai.ui.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimetableOptimizerAITheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = viewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        // Pop up to the start destination to avoid building up a large stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToTimetables = {
                        navController.navigate("timetables")
                    },
                    onNavigateToSubjects = {
                        navController.navigate("subjects")
                    },
                    onNavigateToAnalytics = {
                        navController.navigate("analytics")
                    }
                )
            }
            
            composable("timetables") {
                TimetablesScreen(
                    onNavigateToTimetableDetail = { timetableId ->
                        navController.navigate("timetable_detail/$timetableId")
                    },
                    onNavigateToCreateTimetable = {
                        navController.navigate("create_timetable")
                    }
                )
            }
            
            composable("subjects") {
                SubjectsScreen(
                    onNavigateToSubjectDetail = { subjectId ->
                        navController.navigate("subject_detail/$subjectId")
                    },
                    onNavigateToCreateSubject = {
                        navController.navigate("create_subject")
                    }
                )
            }
            
            composable("analytics") {
                AnalyticsScreen()
            }
            
            composable("settings") {
                SettingsScreen()
            }
            
            // Detail screens
            composable("timetable_detail/{timetableId}") { backStackEntry ->
                val timetableId = backStackEntry.arguments?.getString("timetableId") ?: ""
                TimetableDetailScreen(
                    timetableId = timetableId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable("create_timetable") {
                CreateTimetableScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onTimetableCreated = { timetableId ->
                        navController.navigate("timetable_detail/$timetableId") {
                            popUpTo("timetables")
                        }
                    }
                )
            }
            
            composable("subject_detail/{subjectId}") { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
                SubjectDetailScreen(
                    subjectId = subjectId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable("create_subject") {
                CreateSubjectScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onSubjectCreated = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun TimetableDetailScreen(
    timetableId: String,
    onNavigateBack: () -> Unit
) {
    // Placeholder for timetable detail screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Implementation will be added later
    }
}

@Composable
fun CreateTimetableScreen(
    onNavigateBack: () -> Unit,
    onTimetableCreated: (String) -> Unit
) {
    // Placeholder for create timetable screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Implementation will be added later
    }
}

@Composable
fun SubjectDetailScreen(
    subjectId: String,
    onNavigateBack: () -> Unit
) {
    // Placeholder for subject detail screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Implementation will be added later
    }
}

@Composable
fun CreateSubjectScreen(
    onNavigateBack: () -> Unit,
    onSubjectCreated: () -> Unit
) {
    // Placeholder for create subject screen
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Implementation will be added later
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TimetableOptimizerAITheme {
        MainScreen()
    }
}
