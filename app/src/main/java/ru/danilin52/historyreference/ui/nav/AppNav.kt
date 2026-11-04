package ru.danilin52.historyreference.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.danilin52.historyreference.HistoryApp
import ru.danilin52.historyreference.ui.detail.EventDetailScreen
import ru.danilin52.historyreference.ui.detail.PersonDetailScreen
import ru.danilin52.historyreference.ui.events.EventListScreen
import ru.danilin52.historyreference.ui.persons.PersonSearchScreen

@Composable
fun AppNav() {
    val context = LocalContext.current
    val repository = (context.applicationContext as HistoryApp).repository
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        repository.ensureSeeded()
    }

    NavHost(navController = navController, startDestination = "events") {
        composable("events") {
            EventListScreen(
                repository = repository,
                onEventClick = { id -> navController.navigate("event/$id") },
                onOpenPersons = { navController.navigate("persons") }
            )
        }

        composable("persons") {
            PersonSearchScreen(
                repository = repository,
                onPersonClick = { id -> navController.navigate("person/$id") },
                onBack = { navController.popBackStack() }
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
                onBack = { navController.popBackStack() },
                onPersonClick = { personId -> navController.navigate("person/$personId") }
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
                onBack = { navController.popBackStack() },
                onEventClick = { eventId -> navController.navigate("event/$eventId") }
            )
        }
    }
}