package com.example.soundbeat_test.ui.screens.selected_playlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.soundbeat_test.R
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.ui.audio.AudioPlayerViewModel
import com.example.soundbeat_test.ui.components.UserImage
import com.example.soundbeat_test.ui.screens.playlists.PlaylistScreenViewModel
import com.example.soundbeat_test.ui.screens.search.VinylList

/**
 * Composable que representa la pantalla de una playlist seleccionada.
 *
 * Esta pantalla muestra los detalles de la playlist seleccionada (nombre, ID, canciones)
 * y permite al usuario reproducir todos los vinilos o seleccionar uno para reproducirlo individualmente.
 *
 * Además, obtiene las canciones asociadas a la playlist desde el ViewModel correspondiente
 * y las muestra en una lista interactiva.
 *
 * @param navHostController Controlador de navegación usado para cambiar de pantalla, por ejemplo para volver al `HOME`.
 * @param sharedPlaylistViewModel ViewModel compartido que contiene la playlist actualmente seleccionada.
 * @param audioPlayerViewModel ViewModel responsable de gestionar la reproducción de audio.
 * @param playlistScreenViewModel ViewModel que maneja la lógica relacionada con las playlists y sus canciones.
 */
@Composable
fun SelectedPlaylistScreen(
    navHostController: NavHostController?,
    sharedPlaylistViewModel: SharedPlaylistViewModel,
    audioPlayerViewModel: AudioPlayerViewModel,
    playlistScreenViewModel: PlaylistScreenViewModel
) {
    val playlist = sharedPlaylistViewModel.selectedPlaylist.collectAsState().value
    val screenMode = sharedPlaylistViewModel.mode.collectAsState().value
    val songSource = sharedPlaylistViewModel.songsSource.collectAsState().value
    val songs = playlistScreenViewModel.songs.collectAsState().value

    val songsSource = sharedPlaylistViewModel.songsSource.collectAsState().value

    LaunchedEffect(songSource, playlist?.id) {
        when (songSource) {
            SongSource.LOCALS -> {
                playlist.let {
                    Log.d("SelectedPlaylistScreen", "Playlist ID: ${playlist?.id}")
                    Log.d("SelectedPlaylistScreen", "Playlist canciones:  ${playlist?.songs}")
                    playlistScreenViewModel.obtainLocalPlaylistSongs(playlist?.id ?: -1)
                }
            }

            SongSource.REMOTES -> {
                playlist.let {
                    Log.d("SelectedPlaylistScreen", "Playlist ID: ${playlist?.id}")
                    Log.d("SelectedPlaylistScreen", "playlist's songs:  ${playlist?.songs}")
                    playlistScreenViewModel.obtainRemotePlaylistSongs(playlist?.id ?: -1)
                }
            }
        }
    }

    val reproduce = if (playlist?.songs?.toList()!!.isEmpty()) songs else playlist.songs.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (screenMode == SelectionMode.PLAYLIST) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "Delete playlist",
                    tint = Color(0xFFCB3813),
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .clickable(onClick = {
                            when (songsSource) {
                                SongSource.LOCALS -> {
                                    sharedPlaylistViewModel.deleteLocalPlaylist(playlist)
                                }

                                SongSource.REMOTES -> {
                                    sharedPlaylistViewModel.deleteRemotePlaylist(playlist)
                                }
                            }
                            navHostController?.navigate(ROUTES.HOME) {
                                popUpTo(ROUTES.HOME) { inclusive = true }
                            }
                        })
                )
            }
            Image(
                painter = painterResource(R.drawable.arrow_back),
                contentDescription = "Go back",
                modifier = Modifier
                    .clickable(onClick = {
                        navHostController?.navigate(ROUTES.HOME) {
                            popUpTo(ROUTES.HOME) { inclusive = true }
                        }
                    })
                    .padding(top = 20.dp)
            )
        }

        Spacer(Modifier.padding(top = 20.dp))

        UserImage()

        playlist.let {

            Text(
                text = playlist?.name ?: "Unsigned",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "id = " + playlist?.id.toString(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp),
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.padding(top = 10.dp))

            Button(
                onClick = {
                    audioPlayerViewModel.loadPlaylist(reproduce)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "REPRODUCE VINYLS", fontWeight = FontWeight.Bold
                )
            }

            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                VinylList(
                    albumList = reproduce
                ) { album ->
                    album.url = audioPlayerViewModel.createSongUrl(album).toString()
                    audioPlayerViewModel.loadAndPlayHLS(
                        album
                    )
                    Log.d(
                        "SelectedPlaylistScreen",
                        "Started playing ${album.title} by ${album.author}"
                    )
                }

            }
        }
    }
}

