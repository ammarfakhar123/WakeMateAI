package com.wakemate.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wakemate.ai.ui.screens.alarm.AlarmScreen
import com.wakemate.ai.ui.screens.chat.ChatScreen
import com.wakemate.ai.ui.screens.home.HomeScreen
import com.wakemate.ai.ui.screens.settings.SettingsScreen
import com.wakemate.ai.ui.screens.task.TaskScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAlarms = { navController.navigate(Screen.Alarms.route) },
                onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                onNavigateToChat = { navController.navigate(Screen.Chat.route) }
            )
        }
        composable(Screen.Alarms.route) {
            AlarmScreen()
        }
        composable(Screen.Tasks.route) {
            TaskScreen()
        }
        composable(Screen.Chat.route) {
            ChatScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
