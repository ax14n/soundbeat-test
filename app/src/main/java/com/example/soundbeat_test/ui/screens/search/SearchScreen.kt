package com.example.soundbeat_test.ui.screens.search

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.soundbeat_test.R
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.data.Playlist
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.ui.components.AlbumCard
import com.example.soundbeat_test.ui.screens.selected_playlist.SharedPlaylistViewModel

enum class MODE {
    NORMAL, CREATOR
}

/**
 * Composable que representa la pantalla de búsqueda de álbumes.
 *
 * Muestra una barra de búsqueda y una lista de resultados basada en la consulta introducida
 * por el usuario. Utiliza un `ViewModel` para mantener y actualizar el estado de la búsqueda
 * y los resultados obtenidos.
 *
 * @param navHostController Controlador de navegación opcional. No se usa dentro de esta función,
 * pero está disponible para permitir navegación desde esta pantalla si es necesario.
 * @param searchScreenViewModel Instancia de [SearchScreenViewModel], inyectada por defecto mediante `viewModel()`,
 * que gestiona el estado del texto de búsqueda y la lista de álbumes.
 */
@Preview(showSystemUi = true)
@Composable
fun SearchScreen(
    navHostController: NavHostController? = null,
    searchScreenViewModel: SearchScreenViewModel = viewModel(),
    sharedPlaylistViewModel: SharedPlaylistViewModel? = null,
    mode: MODE = MODE.NORMAL
) {

    val queryState = searchScreenViewModel.textFieldText.collectAsState()
    val listState = searchScreenViewModel.albumList.collectAsState()

    val query = queryState.value
    val list = listState.value

    Scaffold { padding ->
        Column(
            modifier = Modifier.padding(padding), verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            SearchBarWithButton(
                text = query,
                onTextChange = { searchScreenViewModel.onSearchQueryChange(it) },
                onSearch = { query ->
                    searchScreenViewModel.loadAlbums(query)
                })

            navHostController?.let {
                VinylList(
                    albumList = list
                ) { album ->
                    val playlist: Playlist = Playlist(
                        id = 1, name = album.name, songs = setOf(album)
                    )
                    sharedPlaylistViewModel?.updatePlaylist(playlist)

                    when (mode) {
                        MODE.NORMAL -> {
                            navHostController.navigate(ROUTES.SELECTED_PLAYLIST)
                        }

                        MODE.CREATOR -> {
                            navHostController.navigate(ROUTES.PLAYLIST_CREATOR)
                        }
                    }
                    Log.d("SearchScreen", "Navigating to: SELECTED PLAYLIST")
                }
            }

        }
    }
}

/**
 * Agrupa los vinilos traidos del servidor en columna.
 */
@Composable
fun VinylList(
    albumList: List<Album>, onClickedAlbumCover: (Album) -> Unit
) {

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(10.dp)
    ) {
        items(albumList) { song ->
            AlbumCard(
                song
            ) {
                onClickedAlbumCover(song)
            }
        }
    }
}

/**
 * Composable que representa una barra de búsqueda con un campo de texto y un botón de búsqueda.
 * El campo de texto ocupa el espacio disponible, permitiendo al usuario ingresar el texto
 * deseado, mientras que el botón de búsqueda se manti1ene al lado derecho del campo.
 *
 * @param onSearch Función que se invoca cuando el usuario presiona el botón de búsqueda.
 *                 Recibe el texto actual del campo de texto como parámetro.
 */
@Composable
fun SearchBarWithButton(
    text: String, onTextChange: (String) -> Unit, onSearch: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            value = text,
            onValueChange = { onTextChange(it) },
            label = { Text("Introduzca su canción deseada...") },
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = { onSearch(text) }, modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search), contentDescription = "Buscar"
            )
        }
    }
}