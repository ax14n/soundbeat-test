package com.example.soundbeat_test.ui.screens.create_playlist

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.ui.audio.AudioPlayerViewModel
import com.example.soundbeat_test.ui.components.AlbumCard
import com.example.soundbeat_test.ui.components.UserImage

@Preview(showSystemUi = true)
@Composable
fun CreatePlaylist(
    createPlaylistViewModel: CreatePlaylistViewModel? = viewModel(),
    playerViewModel: AudioPlayerViewModel? = viewModel()
) {

    val playlistName = createPlaylistViewModel?.playlistName
    val songsSet = createPlaylistViewModel?.songs?.value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    println("Crear playlist: ${playlistName?.value}")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            ) {
                Text("Crear")
            }

            Button(
                onClick = {
                    println("Playlist descartada")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            ) {
                Text("Descartar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        UserImage()

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = playlistName?.value ?: "",
            onValueChange = { createPlaylistViewModel?.onPlaylistNameChange(it) },
            label = { Text("Nombre de la playlist") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(2.dp))

        SongsListBox(songsSet?.toList() ?: emptyList(), playerViewModel)
    }
}

@Composable
fun SongsListBox(albums: List<Album>, playerViewModel: AudioPlayerViewModel?) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Canciones de la playlist",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = {
                    println("Agregar canción")
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            ) {
                Text("+")
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
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





