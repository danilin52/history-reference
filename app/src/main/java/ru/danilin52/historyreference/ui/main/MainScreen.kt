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
import ru.danilin52.historyreference.preferences.UserPreferences
import ru.danilin52.historyreference.ui.detail.EventDetailScreen
import ru.danilin52.historyreference.ui.detail.PersonDetailScreen
import ru.danilin52.historyreference.ui.events.EventFormScreen
import ru.danilin52.historyreference.ui.events.EventListScreen
import ru.danilin52.historyreference.ui.events.EventPeopleScreen
import ru.danilin52.historyreference.ui.nav.AppRoutes
import ru.danilin52.historyreference.ui.nav.topLevelDestinations
import ru.danilin52.historyreference.ui.onthisday.OnThisDayScreen
import ru.danilin52.historyreference.ui.persons.PersonSearchScreen
import ru.danilin52.historyreference.ui.persons.PersonFormScreen
import ru.danilin52.historyreference.ui.settings.SettingsScreen

@Composable
fun MainScreen(
    repository: HistoryRepository,
    preferences: UserPreferences
) {
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
                    onEventClick = { id -> tabNav.navigate(AppRoutes.eventDetail(id)) },
                    onAddEvent = { tabNav.navigate(AppRoutes.EVENT_CREATE) }
                )
            }

            composable(AppRoutes.PERSONS) {
                PersonSearchScreen(
                    repository = repository,
                    onPersonClick = { id -> tabNav.navigate(AppRoutes.personDetail(id)) },
                    onAddPerson = { tabNav.navigate(AppRoutes.PERSON_CREATE) }
                )
            }

            composable(AppRoutes.ON_THIS_DAY) {
                OnThisDayScreen(
                    repository = repository,
                    onEventClick = { id -> tabNav.navigate(AppRoutes.eventDetail(id)) }
                )
            }

            composable(AppRoutes.SETTINGS) {
                SettingsScreen(preferences = preferences)
            }

            composable(AppRoutes.EVENT_CREATE) {
                EventFormScreen(
                    repository = repository,
                    eventId = null,
                    onBack = { tabNav.popBackStack() },
                    onSaved = { id ->
                        tabNav.popBackStack()
                        tabNav.navigate(AppRoutes.eventDetail(id))
                    }
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
                    },
                    onEdit = { tabNav.navigate(AppRoutes.eventEdit(id)) },
                    onDeleted = {
                        if (!tabNav.popBackStack(AppRoutes.EVENTS, inclusive = false)) {
                            tabNav.navigate(AppRoutes.EVENTS)
                        }
                    },
                    onManagePeople = { tabNav.navigate(AppRoutes.eventPeople(id)) }
                )
            }

            composable(
                route = AppRoutes.EVENT_EDIT,
                arguments = listOf(navArgument(AppRoutes.EVENT_ID) { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong(AppRoutes.EVENT_ID) ?: return@composable
                EventFormScreen(
                    repository = repository,
                    eventId = id,
                    onBack = { tabNav.popBackStack() },
                    onSaved = { tabNav.popBackStack() }
                )
            }

            composable(
                route = AppRoutes.EVENT_PEOPLE,
                arguments = listOf(navArgument(AppRoutes.EVENT_ID) { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong(AppRoutes.EVENT_ID) ?: return@composable
                EventPeopleScreen(
                    repository = repository,
                    eventId = id,
                    onBack = { tabNav.popBackStack() }
                )
            }

            composable(AppRoutes.PERSON_CREATE) {
                PersonFormScreen(
                    repository = repository,
                    personId = null,
                    onBack = { tabNav.popBackStack() },
                    onSaved = { id ->
                        tabNav.popBackStack()
                        tabNav.navigate(AppRoutes.personDetail(id))
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
                    },
                    onEdit = { tabNav.navigate(AppRoutes.personEdit(id)) },
                    onDeleted = {
                        if (!tabNav.popBackStack(AppRoutes.PERSONS, inclusive = false)) {
                            tabNav.navigate(AppRoutes.PERSONS)
                        }
                    }
                )
            }

            composable(
                route = AppRoutes.PERSON_EDIT,
                arguments = listOf(navArgument(AppRoutes.PERSON_ID) { type = NavType.LongType })
            ) { entry ->
                val id = entry.arguments?.getLong(AppRoutes.PERSON_ID) ?: return@composable
                PersonFormScreen(
                    repository = repository,
                    personId = id,
                    onBack = { tabNav.popBackStack() },
                    onSaved = { tabNav.popBackStack() }
                )
            }
        }
    }
}
