package com.example.soundbeat_test.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.soundbeat_test.local.listLocalAlbums
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.network.getServerSongs
import com.example.soundbeat_test.ui.components.AlbumHorizontalList
import com.example.soundbeat_test.ui.screens.selected_playlist.SelectionMode
import com.example.soundbeat_test.ui.screens.selected_playlist.SharedPlaylistViewModel

@Preview(showSystemUi = true)
@Composable
fun HomeScreen(
    navHostController: NavHostController? = null,
    sharedPlaylistViewModel: SharedPlaylistViewModel? = null
) {

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            ListSongs(
                "Remote songs!",
                navHostController = navHostController,
                genre = null,
                sharedPlaylistViewModel = sharedPlaylistViewModel
            )

            ListSongs(
                "Local songs!",
                navHostController = navHostController,
                genre = null,
                sharedPlaylistViewModel = sharedPlaylistViewModel,
                isLocal = true
            )
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
fun ListSongs(
    text: String,
    genre: String?,
    navHostController: NavHostController?,
    sharedPlaylistViewModel: SharedPlaylistViewModel?,
    isLocal: Boolean = false
) {
    var songsList by remember { mutableStateOf<List<Album>>(emptyList<Album>()) }

    LaunchedEffect(Unit) {
        songsList = if (isLocal) {
            listLocalAlbums()
        } else {
            val result = getServerSongs(genre ?: "null")
            if (result.isSuccess) {
                result.getOrNull() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }
    Text(text)
    AlbumHorizontalList(songsList) { item ->
        when (item) {
            is Playlist -> {
                sharedPlaylistViewModel?.setMode(SelectionMode.PLAYLIST)
                sharedPlaylistViewModel?.updatePlaylist(item)
                navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
            }

            is Album -> {
                val playlist = Playlist(
                    id = item.id,
                    name = item.name,
                    songs = setOf(item.copy())
                )
                sharedPlaylistViewModel?.setMode(SelectionMode.SONG)
                sharedPlaylistViewModel?.updatePlaylist(playlist)
                navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
            }
        }
    }
}