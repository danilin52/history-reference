package ru.danilin52.historyreference.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import ru.danilin52.historyreference.ui.nav.AppRoutes
import ru.danilin52.historyreference.ui.nav.topLevelDestinations
import ru.danilin52.historyreference.ui.onthisday.OnThisDayScreen
import ru.danilin52.historyreference.ui.persons.PersonSearchScreen

@Composable
fun MainScreen(repository: HistoryRepository) {
    val tabNav = rememberNavController()
    val backStackEntry by tabNav.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isTopLevelDestination = topLevelDestinations.any { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    }

    Scaffold(
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        val selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == destination.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                tabNav.navigate(destination.route) {
                                    popUpTo(tabNav.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.label
                                )
                            },
                            label = { Text(destination.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = tabNav,
            startDestination = AppRoutes.EVENTS,
            modifier = Modifier.padding(padding)
        ) {
            composable(AppRoutes.EVENTS) {
                EventListScreen(
                    repository = repository,
                    onEventClick = { id -> tabNav.navigate(AppRoutes.eventDetail(id)) }
                )
            }

            composable(AppRoutes.PERSONS) {
                PersonSearchScreen(
                    repository = repository,
                    onPersonClick = { id -> tabNav.navigate(AppRoutes.personDetail(id)) }
                )
            }

            composable(AppRoutes.ON_THIS_DAY) {
                OnThisDayScreen(
                    repository = repository,
                    onEventClick = { id -> tabNav.navigate(AppRoutes.eventDetail(id)) }
                )
            }

            composable(
                route = AppRoutes.EVENT_DETAIL,
                arguments = listOf(navArgument(AppRoutes.EVENT_ID) { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong(AppRoutes.EVENT_ID) ?: return@composable
                EventDetailScreen(
                    repository = repository,
                    eventId = id,
                    onBack = { tabNav.popBackStack() },
                    onPersonClick = { personId ->
                        tabNav.navigate(AppRoutes.personDetail(personId))
                    }
                )
            }

            composable(
                route = AppRoutes.PERSON_DETAIL,
                arguments = listOf(navArgument(AppRoutes.PERSON_ID) { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong(AppRoutes.PERSON_ID) ?: return@composable
                PersonDetailScreen(
                    repository = repository,
                    personId = id,
                    onBack = { tabNav.popBackStack() },
                    onEventClick = { eventId ->
                        tabNav.navigate(AppRoutes.eventDetail(eventId))
                    }
                )
            }
        }
    }
}
