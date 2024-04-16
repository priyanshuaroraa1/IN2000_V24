package no.uio.ifi.in2000.martirhe.appsolution.ui.navigation.navbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import no.uio.ifi.in2000.martirhe.appsolution.ui.navigation.Routes


sealed class BottomNavItem(
    var title: String,
    var icon: ImageVector,
    val route: String,
) {
    object Home :
        BottomNavItem(
            "Home",
            Icons.Default.Home,
            Routes.HOME_SCREEN
        )

    object AboutUs :
        BottomNavItem(
            "About us",
            Icons.Default.Info,
            Routes.ABOUT_US_SCREEN
        )

    object List :
        BottomNavItem(
            "List",
            Icons.Default.Checklist,
            Routes.HOME_SCREEN
        )
}



@Composable
fun BottomNavBar(navController: androidx.navigation.NavController) {

    val items = listOf(
//        BottomNavItem.List,
        BottomNavItem.Home,
        BottomNavItem.AboutUs,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
//        containerColor = MaterialTheme.colorScheme.onPrimary
    ) {
        items.forEach { item ->
            NavigationBarItem(
                label = {
                        Text(text = item.title)
                },
                icon = { Icon(
                    imageVector = item.icon,
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.primaryContainer
                ) },
                selected = currentRoute == item.route,
                onClick = {
                    // Navigate to the item's route
                    navController.navigate(item.route) {

                        // This makes sure the back button takes you to the home screen
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
    }
}