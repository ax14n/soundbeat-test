package com.example.soundbeat_test.ui.screens.create_playlist

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.ui.audio.AudioPlayerViewModel
import com.example.soundbeat_test.ui.components.AlbumCard
import com.example.soundbeat_test.ui.components.UserImage
import com.example.soundbeat_test.ui.screens.search.SearchInteractionMode
import com.example.soundbeat_test.ui.screens.selected_playlist.SharedPlaylistViewModel

@OptIn(UnstableApi::class)
@Composable
fun CreatePlaylistScreen(
    navController: NavHostController,
    createPlaylistViewModel: CreatePlaylistViewModel,
    playerViewModel: AudioPlayerViewModel,
    sharedPlaylistViewModel: SharedPlaylistViewModel,
    creationMode: CreationMode
) {
    val sharedPlaylist = sharedPlaylistViewModel.selectedPlaylist.collectAsState()
    val receivedPlaylist = sharedPlaylist?.value

    LaunchedEffect(receivedPlaylist?.songs?.lastOrNull()) {
        if (receivedPlaylist?.songs?.isNotEmpty() == true) {
            val album: Album = receivedPlaylist.songs.last()
            createPlaylistViewModel.addSong(album)

            Log.d("CreatePlaylistScreen", "${createPlaylistViewModel.songs.value}")
            sharedPlaylistViewModel.clearPlaylist()
        }
    }

    val playlistName = createPlaylistViewModel.playlistName.collectAsState().value
    val songsSet = createPlaylistViewModel.songs.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF5722)),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "mode: $creationMode", color = Color.White, fontStyle = FontStyle.Italic)
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                onClick = {
                    createPlaylistViewModel.clearPlaylistName()
                    createPlaylistViewModel.clearSongsList()
                    navController.navigate(ROUTES.HOME) {
                        popUpTo(ROUTES.HOME) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            ) {
                Text("Discard")
            }
            Button(
                onClick = {
                    createPlaylistViewModel.createPlaylist(creationMode)
                    navController.navigate(ROUTES.HOME) {
                        popUpTo(ROUTES.HOME) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            ) {
                Text("Create")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        UserImage()

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = playlistName,
            onValueChange = { createPlaylistViewModel.onPlaylistNameChange(it) },
            label = { Text("Give your playlist a name!") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(2.dp))

        SongsListBox(
            albums = songsSet?.toList() ?: emptyList(),
            playerViewModel = playerViewModel,
            navController = navController,
            creationMode = creationMode
        )
    }
}

@Composable
fun SongsListBox(
    albums: List<Album>, playerViewModel: AudioPlayerViewModel?, navController: NavHostController?,
    creationMode: CreationMode
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your playlist's songs",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = {
                    navController?.navigate("SEARCH/${SearchInteractionMode.APPEND_TO_PLAYLIST.name}/${creationMode}") {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            ) {
                Text("+")
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(albums.toList()) { album ->
                    AlbumCard(album) {
                        val url: String = playerViewModel?.createSongUrl(album) ?: ""
                        playerViewModel?.loadAndPlayHLS(url, album.name, album.author)
                    }
                }
            }
        }
    }
}





