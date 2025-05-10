package com.example.soundbeat_test

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.soundbeat_test.navigation.GetNavItemList
import com.example.soundbeat_test.navigation.NavItem
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.ui.screens.auth.LoginScreen
import com.example.soundbeat_test.ui.screens.auth.RegisterScreen
import com.example.soundbeat_test.ui.screens.configuration.ConfigurationScreen
import com.example.soundbeat_test.ui.screens.home.HomeScreen
import com.example.soundbeat_test.ui.screens.playlists.PlaylistScreen
import com.example.soundbeat_test.ui.screens.profile.ProfileScreen
import com.example.soundbeat_test.ui.screens.search.SearchScreen
import com.example.soundbeat_test.ui.selected_playlist.SelectedPlaylistScreen
import com.example.soundbeat_test.ui.theme.SoundBeat_TestTheme

/**
 * Actividad principal de la aplicación.
 *
 * Configura el tema general y establece el contenido principal usando Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoundBeat_TestTheme {
                MainApp()
            }
        }
    }
}

/**
 * Punto de entrada principal de la UI.
 *
 * Muestra toda la estructura de navegación y contenido de la app.
 * Se utiliza en el `setContent` de la actividad.
 */
@Preview(showSystemUi = true)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    AppNavigation(navController = navController)
}

/**
 * Controlador de navegación general de la aplicación.
 *
 * Define las rutas principales de la app como login, registro, home y configuración.
 *
 * @param navController Controlador de navegación que gestiona el flujo de pantallas.
 */
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = ROUTES.LOGIN) {
        composable(ROUTES.LOGIN) {
            LoginScreen(navController)
        }
        composable(ROUTES.REGISTER) {
            RegisterScreen(navController)
        }
        composable(ROUTES.HOME) {
            MainScreen(navController)
        }
        composable(ROUTES.SETTINGS) {
            ConfigurationScreen(navController)
        }
        composable(ROUTES.SELECTED_PLAYLIST) {
            SelectedPlaylistScreen(navController)
        }
    }
}

/**
 * Pantalla principal que contiene la barra de navegación inferior y el contenido dinámico.
 *
 * @param navHosController Controlador de navegación necesario si se quieren realizar cambios de pantalla desde esta sección.
 */
@Composable
fun MainScreen(navHosController: NavHostController) {
    val navItemList: List<NavItem> = GetNavItemList()

    var selectedIndex by remember {
        mutableIntStateOf(0)
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {}, bottomBar = {
        BottomNavigationBar(navItemList, selectedIndex) { selectedIndex = it }
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            ContentScreen(
                selectedIndex = selectedIndex, navHostController = navHosController
            )
        }
    }

}

/**
 * Barra de navegación inferior que permite seleccionar diferentes secciones de la aplicación.
 *
 * @param navItemList Lista de elementos de navegación disponibles.
 * @param selectedIndex Índice del elemento actualmente seleccionado.
 * @return Índice actualizado tras seleccionar un nuevo elemento.
 */
@Composable
private fun BottomNavigationBar(
    navItemList: List<NavItem>, selectedIndex: Int, onInteraction: (Int) -> Unit
) {
    NavigationBar {
        navItemList.forEachIndexed { index, navItem ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { onInteraction(index) },
                icon = {
                    Icon(
                        imageVector = navItem.icon, contentDescription = "Element"
                    )
                },
                label = { Text(text = navItem.label) })
        }
    }
}

/**
 * Renderiza el contenido dinámico de la pantalla dependiendo del índice seleccionado en la barra inferior.
 *
 * @param selectedIndex Índice del elemento seleccionado que define qué pantalla se muestra.
 */
@Composable
fun ContentScreen(
    selectedIndex: Int, navHostController: NavHostController
) {
    Log.d("MainActivity", "NavigationBar index: $selectedIndex")
    when (selectedIndex) {
        0 -> HomeScreen(navHostController)
        1 -> PlaylistScreen(navHostController)
        2 -> SearchScreen(navHostController)
        3 -> ProfileScreen(navHostController)
    }
}