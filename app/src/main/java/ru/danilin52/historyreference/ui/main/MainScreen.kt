package ru.danilin52.historyreference.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.danilin52.historyreference.data.HistoryRepository
import ru.danilin52.historyreference.ui.detail.EventDetailScreen
import ru.danilin52.historyreference.ui.detail.PersonDetailScreen
import ru.danilin52.historyreference.ui.events.EventListScreen
import ru.danilin52.historyreference.ui.onthisday.OnThisDayScreen
import ru.danilin52.historyreference.ui.persons.PersonSearchScreen

private data class Tab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val tabs = listOf(
    Tab("events", "События", Icons.Filled.History),
    Tab("persons", "Личности", Icons.Filled.Person),
    Tab("onthisday", "В этот день", Icons.Filled.Notifications)
)

@Composable
fun MainScreen(repository: HistoryRepository) {
    val tabNav = rememberNavController()
    val backStackEntry by tabNav.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == tab.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            tabNav.navigate(tab.route) {
                                popUpTo(tabNav.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = tabNav,
            startDestination = "events",
            modifier = Modifier.padding(padding)
        ) {
            composable("events") {
                EventListScreen(
                    repository = repository,
                    onEventClick = { id -> tabNav.navigate("event/$id") }
                )
            }

            composable("persons") {
                PersonSearchScreen(
                    repository = repository,
                    onPersonClick = { id -> tabNav.navigate("person/$id") }
                )
            }

            composable("onthisday") {
                OnThisDayScreen(
                    repository = repository,
                    onEventClick = { id -> tabNav.navigate("event/$id") }
                )
            }

            composable(
                route = "event/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: return@composable
                EventDetailScreen(
                    repository = repository,
                    eventId = id,
                    onBack = { tabNav.popBackStack() },
                    onPersonClick = { personId -> tabNav.navigate("person/$personId") }
                )
            }

            composable(
                route = "person/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong("id") ?: return@composable
                PersonDetailScreen(
                    repository = repository,
                    personId = id,
                    onBack = { tabNav.popBackStack() },
                    onEventClick = { eventId -> tabNav.navigate("event/$eventId") }
                )
            }
        }
    }
}