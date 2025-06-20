package com.example.soundbeat_test

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.soundbeat_test.local.LocalConfig
import com.example.soundbeat_test.navigation.GetNavItemList
import com.example.soundbeat_test.navigation.NavItem
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.network.ServerConfig
import com.example.soundbeat_test.ui.audio.AudioPlayerViewModel
import com.example.soundbeat_test.ui.audio.MusicPlayerDropdownMenu
import com.example.soundbeat_test.ui.screens.auth.LoginScreen
import com.example.soundbeat_test.ui.screens.auth.RegisterScreen
import com.example.soundbeat_test.ui.screens.configuration.ConfigurationScreen
import com.example.soundbeat_test.ui.screens.create_playlist.CreatePlaylistScreen
import com.example.soundbeat_test.ui.screens.create_playlist.CreatePlaylistViewModel
import com.example.soundbeat_test.ui.screens.create_playlist.PlaylistOrigin
import com.example.soundbeat_test.ui.screens.home.HomeScreen
import com.example.soundbeat_test.ui.screens.playlists.PlaylistScreen
import com.example.soundbeat_test.ui.screens.playlists.PlaylistScreenViewModel
import com.example.soundbeat_test.ui.screens.profile.ProfileScreen
import com.example.soundbeat_test.ui.screens.search.SearchInteractionMode
import com.example.soundbeat_test.ui.screens.search.SearchScreen
import com.example.soundbeat_test.ui.screens.selected_playlist.SelectedPlaylistScreen
import com.example.soundbeat_test.ui.screens.selected_playlist.SharedPlaylistViewModel
import com.example.soundbeat_test.ui.screens.selected_playlist.SongSource
import com.example.soundbeat_test.ui.theme.SoundBeat_TestTheme

/**
 * Actividad principal de la aplicación.
 *
 * Configura el tema general y establece el contenido principal usando Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServerConfig.init(this)
        LocalConfig.init(this)
        enableEdgeToEdge()
        setContent {
            var isDarkTheme by remember {
                mutableStateOf(
                    getSharedPreferences("UserInfo", MODE_PRIVATE).getBoolean(
                        "is_dark_theme", false
                    )
                )
            }

            val toggleTheme: () -> Unit = {
                isDarkTheme = !isDarkTheme
                getSharedPreferences("UserInfo", MODE_PRIVATE).edit()
                    .putBoolean("is_dark_theme", isDarkTheme).apply()
            }

            SoundBeat_TestTheme(darkTheme = isDarkTheme) {

                val audioPlayerViewModel: AudioPlayerViewModel = viewModel()
                val sharedPlaylistViewModel: SharedPlaylistViewModel = viewModel()
                val playlistScreenViewModel: PlaylistScreenViewModel = viewModel()
                val createPlaylistViewModel: CreatePlaylistViewModel = viewModel()
                Scaffold { it ->
                    Column(Modifier.padding(it)) {
                        MusicPlayerDropdownMenu(
                            audioPlayerViewModel = audioPlayerViewModel,
                        ) {
                            MainApp(
                                audioPlayerViewModel = audioPlayerViewModel,
                                sharedPlaylistViewModel = sharedPlaylistViewModel,
                                playlistScreenViewModel = playlistScreenViewModel,
                                createPlaylistViewModel = createPlaylistViewModel,
                                onToggleTheme = toggleTheme,
                            )
                        }
                    }
                }
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
@Composable
fun MainApp(
    audioPlayerViewModel: AudioPlayerViewModel? = null,
    sharedPlaylistViewModel: SharedPlaylistViewModel? = null,
    playlistScreenViewModel: PlaylistScreenViewModel? = null,
    createPlaylistViewModel: CreatePlaylistViewModel,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()

    AppNavigation(
        navController = navController,
        sharedPlaylistViewModel = sharedPlaylistViewModel,
        audioPlayerViewModel = audioPlayerViewModel,
        playlistScreenViewModel = playlistScreenViewModel,
        createPlaylistViewModel = createPlaylistViewModel,
        onToggleTheme = onToggleTheme,
    )
}

/**
 * Controlador de navegación general de la aplicación.
 *
 * Define las rutas principales de la app como login, registro, home y configuración.
 *
 * @param navController Controlador de navegación que gestiona el flujo de pantallas.
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    sharedPlaylistViewModel: SharedPlaylistViewModel?,
    audioPlayerViewModel: AudioPlayerViewModel?,
    playlistScreenViewModel: PlaylistScreenViewModel?,
    createPlaylistViewModel: CreatePlaylistViewModel,
    onToggleTheme: () -> Unit
) {
    NavHost(navController = navController, startDestination = ROUTES.LOGIN) {
        composable(ROUTES.LOGIN) {
            LoginScreen(navController)
        }
        composable(ROUTES.REGISTER) {
            RegisterScreen(navController)
        }
        composable(ROUTES.HOME) {
            MainScreen(
                navHostController = navController,
                sharedPlaylistViewModel = sharedPlaylistViewModel,
                playlistScreenViewModel = playlistScreenViewModel
            )
        }
        composable(ROUTES.PLAYLIST) {
            PlaylistScreen(
                navHostController = navController,
                playlistScreenViewModel = playlistScreenViewModel!!,
                sharedPlaylistViewModel = sharedPlaylistViewModel!!
            )
        }
        composable(ROUTES.PROFILE) {
            ProfileScreen(navController)
        }
        composable(ROUTES.SETTINGS) {
            ConfigurationScreen(
                navController, onToggleTheme = onToggleTheme
            )
        }
        composable(ROUTES.SELECTED_PLAYLIST) {
            SelectedPlaylistScreen(
                navHostController = navController,
                sharedPlaylistViewModel = sharedPlaylistViewModel!!,
                audioPlayerViewModel = audioPlayerViewModel!!,
                playlistScreenViewModel = playlistScreenViewModel!!
            )
        }
        composable("SEARCH/{mode}/{procedence}/{edit}") { backStackEntry ->
            val modeArg = backStackEntry.arguments?.getString("mode")
            val originArg = backStackEntry.arguments?.getString("procedence")
            val editArg = backStackEntry.arguments?.getString("edit")

            val onClickInteraction = try {
                SearchInteractionMode.valueOf(
                    modeArg ?: SearchInteractionMode.REPRODUCE_ON_SELECT.name
                )
            } catch (_: IllegalArgumentException) {
                SearchInteractionMode.REPRODUCE_ON_SELECT
            }
            Log.d("MainActivity", "${onClickInteraction.name}")

            val procedence = try {
                PlaylistOrigin.valueOf(originArg ?: "null")
            } catch (_: IllegalArgumentException) {
                PlaylistOrigin.OFFLINE_PLAYLIST
            }
            Log.d("MainActivity", "${procedence.name}")

            val edit = editArg?.toBooleanStrictOrNull() == true
            Log.d("MainActivity", "$edit")

            SearchScreen(
                navHostController = navController,
                sharedPlaylistViewModel = sharedPlaylistViewModel!!,
                searchInteractionMode = onClickInteraction,
                procedence = procedence,
                editMode = edit
            )
        }
        composable("PLAYLIST_CREATOR/{mode}") { backStackEntry ->
            val modeArg = backStackEntry.arguments?.getString("mode")
            val playlistOrigin = try {
                PlaylistOrigin.valueOf(
                    modeArg ?: PlaylistOrigin.OFFLINE_PLAYLIST.name
                )
            } catch (_: IllegalArgumentException) {
                PlaylistOrigin.OFFLINE_PLAYLIST
            }
            Log.d("MainActivity", "${playlistOrigin.name}")

            sharedPlaylistViewModel?.setSongsSource(songsSource = SongSource.REMOTES)
            CreatePlaylistScreen(
                navController = navController,
                playerViewModel = audioPlayerViewModel!!,
                sharedPlaylistViewModel = sharedPlaylistViewModel!!,
                createPlaylistViewModel = createPlaylistViewModel,
                playlistOrigin = playlistOrigin
            )
        }
    }
}

/**
 * Pantalla principal que contiene la barra de navegación inferior y el contenido dinámico.
 *
 * @param navHosController Controlador de navegación necesario si se quieren realizar cambios de pantalla desde esta sección.
 */
@Composable
fun MainScreen(
    navHostController: NavHostController,
    sharedPlaylistViewModel: SharedPlaylistViewModel?,
    playlistScreenViewModel: PlaylistScreenViewModel?
) {
    val navItemList: List<NavItem> = GetNavItemList()
    var selectedIndex by remember { mutableIntStateOf(0) }

    var showcaseStep by remember { mutableIntStateOf(0) }

    val context: Context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(bottomBar = {
            BottomNavigationBar(navItemList, selectedIndex) { selectedIndex = it }
        }) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                ContentScreen(
                    selectedIndex = selectedIndex,
                    navHostController = navHostController,
                    playlistScreenViewModel = playlistScreenViewModel,
                    sharedPlaylistViewModel = sharedPlaylistViewModel
                )
            }
        }

        val prefs = remember { context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE) }
        val tutorial = remember {
            derivedStateOf {
                prefs.getString("tutorial", "ON")
            }
        }.value

        if (showcaseStep <= 3 && tutorial != "OFF") {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    // Con esto bloqueo los inputs para que no pasen por detrás cuando la guía está
                    // en proceso.
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent()
                            }
                        }
                    })

            // Tu guía
            Guide(
                step = showcaseStep, onNext = {
                    showcaseStep++
                }, context = context
            ) {
                selectedIndex = it
            }
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
    NavigationBar() {
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
 * @param navHostController Controlador encargado de la navegación de pantallas.
 */
@Composable
fun ContentScreen(
    selectedIndex: Int,
    navHostController: NavHostController,
    sharedPlaylistViewModel: SharedPlaylistViewModel?,
    playlistScreenViewModel: PlaylistScreenViewModel?
) {
    Log.d("MainActivity", "NavigationBar index: $selectedIndex")

    when (selectedIndex) {
        0 -> HomeScreen(navHostController, sharedPlaylistViewModel)
        1 -> PlaylistScreen(
            navHostController = navHostController,
            playlistScreenViewModel = playlistScreenViewModel!!,
            sharedPlaylistViewModel = sharedPlaylistViewModel!!
        )

        2 -> SearchScreen(
            navHostController = navHostController,
            sharedPlaylistViewModel = sharedPlaylistViewModel!!,
            searchInteractionMode = SearchInteractionMode.REPRODUCE_ON_SELECT
        )

        3 -> ProfileScreen(navHostController)
    }
}

@Composable
fun Guide(
    step: Int,
    modifier: Modifier = Modifier,
    onNext: () -> Unit,
    context: Context,
    onIndexChange: (Int) -> Unit,
) {
    val prefs = remember { context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit() }

    when (step) {
        0 -> Box(modifier = modifier.fillMaxSize()) {
            Text(
                "¡Aquí puedes ver las canciones disponibles!\nNext ->",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { onNext() }
                    .padding(16.dp),
                color = Color.White)
        }

        1 -> Box(modifier = modifier.fillMaxSize()) {
            onIndexChange(1) // Playlist
            Text(
                "¡Aquí puedes ver las playlists locales y remotas!\nNext ->",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { onNext() }
                    .padding(16.dp),
                color = Color.White)
        }

        2 -> Box(modifier = modifier.fillMaxSize()) {
            onIndexChange(2) // Search
            Text(
                "¡Aquí puedes ver el búscador de canciones!\n" + "¡Aquí podrás filtrar y buscar canciones remotas o locales!" + "\nNext ->",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { onNext() }
                    .padding(16.dp),
                color = Color.White)
        }

        3 -> Box(modifier = modifier.fillMaxSize()) {
            onIndexChange(3) // Profile
            Text(
                "¡Aquí he tu perfil!\n¡Clickeame para terminar la guía! ->",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { onNext() }
                    .padding(16.dp),
                color = Color.White)
            prefs.putString("tutorial", "OFF").apply()

        }

        else -> {}
    }
}
