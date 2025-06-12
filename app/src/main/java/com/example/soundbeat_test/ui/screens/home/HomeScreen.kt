package com.example.soundbeat_test.ui.screens.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.data.Playlist
import com.example.soundbeat_test.local.LocalConfig
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.network.getServerSongs
import com.example.soundbeat_test.ui.components.AlbumHorizontalList
import com.example.soundbeat_test.ui.components.ImageGif
import com.example.soundbeat_test.ui.screens.selected_playlist.SelectionMode
import com.example.soundbeat_test.ui.screens.selected_playlist.SharedPlaylistViewModel
import com.example.soundbeat_test.ui.screens.selected_playlist.SongSource

@Preview(showSystemUi = true)
@Composable
fun HomeScreen(
    navHostController: NavHostController? = null,
    sharedPlaylistViewModel: SharedPlaylistViewModel? = null
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE) }
    val email = remember {
        derivedStateOf {
            prefs.getString("email", "OFFLINE")
        }
    }.value

    Spacer(modifier = Modifier.height(8.dp))

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val url =
            "https://media1.giphy.com/media/v1.Y2lkPTc5MGI3NjExeHlsYW5haXFhcGpidjd0emd5ZHZsbDJ0MDlvM3NlMGh6aWI3bTJkZyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/0xl8wFcBg6OPU6UzCh/giphy.gif"

        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageGif(
                    imageSource = url,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                ) {}

                Spacer(modifier = Modifier.height(12.dp)) // espacio entre gif y texto

                Text(
                    text = "Welcome to Sound-Beat!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Heard about you!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp)
            ) {
                if (email == "OFFLINE") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        NoSongsFoundMessage("You're in OFFLINE MODE")
                    }
                } else {
                    ListSongs(
                        "Remote songs!",
                        navHostController = navHostController,
                        genre = null,
                        sharedPlaylistViewModel = sharedPlaylistViewModel
                    )
                }

                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(10.dp)
                ) {
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
            LocalConfig.listLocalAlbums()
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
    Spacer(modifier = Modifier.padding(5.dp))
    AlbumHorizontalList(songsList) { item ->
        when (item) {
            is Playlist -> {
                sharedPlaylistViewModel?.setSelectionMode(SelectionMode.PLAYLIST)
                sharedPlaylistViewModel?.updatePlaylist(item)
                navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
            }

            is Album -> {
                val playlist = Playlist(
                    id = item.id, name = item.title, songs = setOf(item.copy())
                )

                val songSource = if (item.isLocal) SongSource.LOCALS else SongSource.REMOTES

                sharedPlaylistViewModel?.setSongsSource(songSource)
                sharedPlaylistViewModel?.setSelectionMode(SelectionMode.SONG)
                sharedPlaylistViewModel?.updatePlaylist(playlist)
                navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
            }
        }
    }
}

@Composable
fun NoSongsFoundMessage(customizedMessage: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.MusicNote,
            contentDescription = "Nota musical",
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = customizedMessage ?: "No playlists were found",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

