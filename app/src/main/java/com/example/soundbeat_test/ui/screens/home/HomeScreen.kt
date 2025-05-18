package com.example.soundbeat_test.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.data.Playlist
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.network.getServerSongs
import com.example.soundbeat_test.ui.components.AlbumHorizontalList
import com.example.soundbeat_test.ui.components.LeftColumnRightLargeGifLayout
import com.example.soundbeat_test.ui.selected_playlist.SharedPlaylistViewModel

@Preview(showSystemUi = true)
@Composable
fun HomeScreen(
    navHostController: NavHostController? = null,
    sharedPlaylistViewModel: SharedPlaylistViewModel? = null
) {

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(15.dp)

        ) {
            // LeftColumnRightLargeGifLayout()
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                ListServerSongs(
                    "¡Canciones del servidor!",
                    navHostController = navHostController,
                    genre = null,
                    sharedPlaylistViewModel = sharedPlaylistViewModel
                )
                ListServerSongs(
                    "¡Tus canciones favoritas remotas!",
                    navHostController = navHostController,
                    genre = null,
                    sharedPlaylistViewModel = sharedPlaylistViewModel,
                )
                ListServerSongs(
                    "¡Canciones locales!",
                    navHostController = navHostController,
                    genre = null,
                    sharedPlaylistViewModel = sharedPlaylistViewModel
                )
                ListServerSongs(
                    "¡Tus canciones favoritas locales!",
                    navHostController = navHostController,
                    genre = null,
                    sharedPlaylistViewModel = sharedPlaylistViewModel
                )
            }
        }
    }
}

/**
 * Composable que muestra una lista horizontal de álbumes o playlists obtenidos del servidor.
 *
 * Este componente realiza una llamada asíncrona para obtener los álbumes desde el servidor
 * en base a un género determinado. Una vez obtenidos, se renderizan como ítems en una lista horizontal
 * y permiten la navegación a una pantalla de playlist seleccionada al hacer clic.
 *
 * @param text Texto que se muestra como título o encabezado de la sección.
 * @param genre Género musical para filtrar los álbumes obtenidos del servidor. Si es null, se usa "null" como valor.
 * @param navHostController Controlador de navegación para cambiar de pantalla al seleccionar un álbum o playlist.
 * @param sharedPlaylistViewModel ViewModel compartido que permite pasar datos a la pantalla de playlist seleccionada.
 */

@Composable
fun ListServerSongs(
    text: String,
    genre: String?,
    navHostController: NavHostController?,
    sharedPlaylistViewModel: SharedPlaylistViewModel?
) {
    var songsList by remember { mutableStateOf<List<Album>>(emptyList<Album>()) }
    LaunchedEffect(Unit) {
        val result = getServerSongs(genre ?: "null")
        if (result.isSuccess) {
            songsList = result.getOrNull() ?: emptyList()
        }
    }
    Text(text)
    AlbumHorizontalList(songsList, sharedPlaylistViewModel) { item ->
        when (item) {
            is Playlist -> {
                sharedPlaylistViewModel?.updatePlaylist(item)
                navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
            }

            is Album -> {
                val playlist = Playlist(
                    id = item.id,
                    name = item.name,
                    songs = setOf(item.copy())
                )
                sharedPlaylistViewModel?.updatePlaylist(playlist)
                navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
            }
        }
    }
}