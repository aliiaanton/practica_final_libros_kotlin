package com.example.practicafinallibros.navigation

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.practicafinallibros.ui.screen.admin.AdminUserListScreen
import com.example.practicafinallibros.ui.screen.auth.LoginScreen
import com.example.practicafinallibros.ui.screen.auth.ProfileScreen
import com.example.practicafinallibros.ui.screen.auth.RegisterScreen
import com.example.practicafinallibros.ui.screen.books.BookDetailScreen
import com.example.practicafinallibros.ui.screen.books.BookFormScreen
import com.example.practicafinallibros.ui.screen.books.BookListScreen
import com.example.practicafinallibros.ui.screen.settings.SettingsScreen
import com.example.practicafinallibros.ui.screen.explore.ExploreScreen
import com.example.practicafinallibros.ui.viewmodel.AdminViewModel
import com.example.practicafinallibros.ui.viewmodel.AuthViewModel
import com.example.practicafinallibros.ui.viewmodel.BookViewModel
import com.example.practicafinallibros.ui.viewmodel.OpenLibraryViewModel
import com.example.practicafinallibros.ui.viewmodel.SettingsViewModel

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    bookViewModel: BookViewModel,
    openLibraryViewModel: OpenLibraryViewModel,
    adminViewModel: AdminViewModel,
    settingsViewModel: SettingsViewModel,
    context: Context
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isAuthScreen = currentRoute == Routes.LOGIN || currentRoute == Routes.REGISTER

    val bottomNavItems = mutableListOf(
        BottomNavItem(Routes.BOOK_LIST, "Mis Libros", Icons.Default.Book),
        BottomNavItem(Routes.EXPLORE, "Explorar", Icons.Default.Explore)
    )

    if (authViewModel.isAdmin) {
        bottomNavItems.add(BottomNavItem(Routes.ADMIN, "Admin", Icons.Default.People))
    }

    bottomNavItems.add(BottomNavItem(Routes.SETTINGS, "Ajustes", Icons.Default.Settings))

    Scaffold(
        bottomBar = {
            if (!isAuthScreen) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Routes.BOOK_LIST) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = if (authViewModel.isLoggedIn) Routes.BOOK_LIST else Routes.LOGIN
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onGoToRegister = { navController.navigate(Routes.REGISTER) },
                        onLoginSuccess = {
                            navController.navigate(Routes.BOOK_LIST) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.REGISTER) {
                    RegisterScreen(
                        authViewModel = authViewModel,
                        onBack = { navController.popBackStack() },
                        onRegisterSuccess = {
                            navController.navigate(Routes.BOOK_LIST) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.PROFILE) {
                    ProfileScreen(
                        authViewModel = authViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable(Routes.BOOK_LIST) {
                    BookListScreen(
                        bookViewModel = bookViewModel,
                        authViewModel = authViewModel,
                        onAddBook = { navController.navigate(Routes.getFormRoute()) },
                        onBookClick = { bookId -> navController.navigate(Routes.getDetailRoute(bookId)) }
                    )
                }

                composable(
                    route = Routes.BOOK_FORM,
                    arguments = listOf(navArgument("bookId") { type = NavType.StringType; nullable = true })
                ) { backStackEntry ->
                    val bookIdStr = backStackEntry.arguments?.getString("bookId")
                    val bookId = bookIdStr?.toIntOrNull()
                    BookFormScreen(
                        bookViewModel = bookViewModel,
                        authViewModel = authViewModel,
                        context = context,
                        bookId = bookId,
                        onBack = { navController.popBackStack() },
                        onSaved = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Routes.BOOK_DETAIL,
                    arguments = listOf(navArgument("bookId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
                    BookDetailScreen(
                        bookViewModel = bookViewModel,
                        authViewModel = authViewModel,
                        bookId = bookId,
                        context = context,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Routes.getFormRoute(bookId)) },
                        onDeleted = { navController.popBackStack() }
                    )
                }

                composable(Routes.EXPLORE) {
                    ExploreScreen(
                        openLibraryViewModel = openLibraryViewModel,
                        bookViewModel = bookViewModel,
                        authViewModel = authViewModel,
                        context = context
                    )
                }

                composable(Routes.ADMIN) {
                    AdminUserListScreen(
                        adminViewModel = adminViewModel,
                        authViewModel = authViewModel
                    )
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(
                        settingsViewModel = settingsViewModel,
                        authViewModel = authViewModel,
                        onLogout = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
                    )
                }
            }
        }
    }
}