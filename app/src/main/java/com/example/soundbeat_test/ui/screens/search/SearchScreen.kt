package com.example.soundbeat_test.ui.screens.search

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
import com.example.soundbeat_test.ui.screens.search.SearchInteractionMode.APPEND_TO_PLAYLIST
import com.example.soundbeat_test.ui.screens.search.SearchInteractionMode.REPRODUCE_ON_SELECT
import com.example.soundbeat_test.ui.screens.selected_playlist.SharedPlaylistViewModel

/**
 * Estos son los modos en los que funciona la interfáz.
 * @param REPRODUCE_ON_SELECT: Reproduce las canciones pinchadas por el usuario.
 * @param APPEND_TO_PLAYLIST: Cuando `SearchScreen` es llamada para crear una playlist, usa
 * este modo. Cambia su funcionamiento para en vez de reproducirlas, las sube a
 * `SharedPlaylistViewModel.kt`
 */
enum class SearchInteractionMode {
    REPRODUCE_ON_SELECT, APPEND_TO_PLAYLIST
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
    searchInteractionMode: SearchInteractionMode = REPRODUCE_ON_SELECT,
) {

    val queryState = searchScreenViewModel.textFieldText.collectAsState()
    val listState = searchScreenViewModel.albumList.collectAsState()

    val query = queryState.value
    val list = listState.value

    val isFilterVisible = searchScreenViewModel.isFilterVisible.collectAsState().value
    val isChecked = searchScreenViewModel.isChecked.collectAsState().value
    val selectedGenres = searchScreenViewModel.selectedGenres.collectAsState().value

    SearchBarWithButton(
        text = query,
        onTextChange = { searchScreenViewModel.onSearchQueryChange(it) },
        onSearch = { query ->
            searchScreenViewModel.fillSongsList(query)
        })

    Column {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { searchScreenViewModel.toggleFilterVisibility() }) {
            Text(if (isFilterVisible) "Ocultar filtros" else "Mostrar filtros")
        }

        AnimatedVisibility(visible = isFilterVisible) {
            DropdownFiltersMenu(
                isChecked = isChecked,
                selectedGenres = selectedGenres,
                onSwitchToggle = { searchScreenViewModel.alternateSwitch() },
                onGenreToggle = { searchScreenViewModel.toggleGenreInSongsFilter(it) }
            )
        }

        // Aquí puedes seguir con la lista o lo que venga después
        Spacer(modifier = Modifier.height(16.dp))
    }

    navHostController?.let {
        VinylList(
            albumList = list
        ) { album ->
            val playlist = Playlist(
                id = 1, name = album.name, songs = setOf(album)
            )
            sharedPlaylistViewModel?.updatePlaylist(playlist)

            when (searchInteractionMode) {
                REPRODUCE_ON_SELECT -> {
                    navHostController.navigate(ROUTES.SELECTED_PLAYLIST)
                }

                APPEND_TO_PLAYLIST -> {
                    navHostController.navigate(ROUTES.PLAYLIST_CREATOR)
                }
            }
            Log.d("SearchScreen", "Navigating to: SELECTED PLAYLIST")
        }
    }

}


/**
 * Menú que despliega los filtros de búsqueda.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DropdownFiltersMenu(
    isChecked: Boolean,
    selectedGenres: Set<Genres>,
    onSwitchToggle: () -> Unit,
    onGenreToggle: (Genres) -> Unit
) {
    val allGenres = Genres.entries

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isChecked, onCheckedChange = {
                    onSwitchToggle()
                })
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isChecked) "Local" else "Remoto",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "¡Filtra tus canciones a tu gusto!",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
        ) {
            allGenres.forEach { genre ->
                FilterChip(selected = selectedGenres.contains(element = genre), onClick = {
                    onGenreToggle(genre)
                }, label = {
                    Text(genre.displayName)
                })
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