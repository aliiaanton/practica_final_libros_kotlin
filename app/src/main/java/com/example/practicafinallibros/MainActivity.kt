package com.example.practicafinallibros

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.practicafinallibros.data.local.DatabaseCallback
import com.example.practicafinallibros.data.local.database.AppDatabase
import com.example.practicafinallibros.data.remote.RetrofitClient
import com.example.practicafinallibros.data.repository.AuthRepository
import com.example.practicafinallibros.data.repository.BookRepository
import com.example.practicafinallibros.data.repository.OpenLibraryRepository
import com.example.practicafinallibros.data.repository.SettingsRepository
import com.example.practicafinallibros.data.repository.UserRepository
import com.example.practicafinallibros.navigation.AppNavGraph
import com.example.practicafinallibros.notifications.NotificationHelper
import com.example.practicafinallibros.ui.theme.PracticaFinalLibrosTheme
import com.example.practicafinallibros.ui.viewmodel.AdminViewModel
import com.example.practicafinallibros.ui.viewmodel.AuthViewModel
import com.example.practicafinallibros.ui.viewmodel.BookViewModel
import com.example.practicafinallibros.ui.viewmodel.OpenLibraryViewModel
import com.example.practicafinallibros.ui.viewmodel.SettingsViewModel
import com.example.practicafinallibros.util.LocaleHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val settingsRepository = SettingsRepository(newBase)
        val language = runBlocking {
            settingsRepository.observeLanguage().first()
        }
        super.attachBaseContext(LocaleHelper.updateResources(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createChannelIfNeeded(this)

        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "libros-db"
        )
            .fallbackToDestructiveMigration(true)
            .addCallback(DatabaseCallback())
            .build()

        val settingsRepository = SettingsRepository(applicationContext)
        val bookRepository = BookRepository(database.bookDao(), database.userFavoriteDao())
        val authRepository = AuthRepository(RetrofitClient.usersApi, settingsRepository)
        val userRepository = UserRepository(RetrofitClient.usersApi, database.userDao())
        val openLibraryRepository = OpenLibraryRepository(RetrofitClient.booksApi)

        val settingsViewModel = SettingsViewModel(settingsRepository)
        val authViewModel = AuthViewModel(authRepository, settingsRepository)
        val bookViewModel = BookViewModel(bookRepository)
        val adminViewModel = AdminViewModel(userRepository)
        val openLibraryViewModel = OpenLibraryViewModel(openLibraryRepository)

        setContent {
            val darkMode by settingsViewModel.darkModeFlow.collectAsState(initial = false)
            
            PracticaFinalLibrosTheme(darkTheme = darkMode) {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    bookViewModel = bookViewModel,
                    openLibraryViewModel = openLibraryViewModel,
                    adminViewModel = adminViewModel,
                    settingsViewModel = settingsViewModel,
                    context = this@MainActivity
                )
            }
        }
    }
}
