package com.example.soundbeat_test.ui.audio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.soundbeat_test.ui.selected_playlist.SharedPlaylistViewModel

/**
 * Composable que implementa un BottomSheet con controles de reproducción de música.
 *
 * @param audioPlayerViewModel ViewModel encargado de la reproducción de canciones.
 * @param content Contenido principal que se muestra detrás del reproductor.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerBottomSheet(
    audioPlayerViewModel: AudioPlayerViewModel,
    sharedPlaylistViewModel: SharedPlaylistViewModel,
    content: @Composable () -> Unit
) {
    val bottomSheetState = rememberBottomSheetScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    val isPlaying by audioPlayerViewModel.isPlaying.collectAsState()
    val currentMediaItem by audioPlayerViewModel.currentMediaItem.collectAsState()

    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetContent = {
            MusicPlayerControls(
                isPlaying = isPlaying,
                currentTrack = currentMediaItem?.mediaMetadata?.title?.toString() ?: "Sin título",
                onPlayPauseClick = {
                    audioPlayerViewModel.playPause()
                },
                onNextTrackClick = {
                    audioPlayerViewModel.skipToNext()
                },
                onPreviousTrackClick = {
                    audioPlayerViewModel.skipToPrevious()
                },
                onAddToFavorites = {
                    audioPlayerViewModel.addToFavorites()
                },
                onSaveTrack = {
                    audioPlayerViewModel.saveTrack()
                }
            )
        },
        sheetPeekHeight = 56.dp,
        content = {
            content()
        }
    )
}

/**
 * Composable que muestra los controles de reproducción de música.
 *
 * @param isPlaying Estado que indica si hay reproducción activa.
 * @param currentTrack Nombre de la canción actual.
 * @param onPlayPauseClick Acción al pulsar el botón de reproducción/pausa.
 * @param onNextTrackClick Acción al pulsar el botón de siguiente canción.
 * @param onPreviousTrackClick Acción al pulsar el botón de canción anterior.
 * @param onAddToFavorites Acción al pulsar el botón de favoritos.
 * @param onSaveTrack Acción al pulsar el botón de salvar.
 */
@Composable
fun MusicPlayerControls(
    isPlaying: Boolean,
    currentTrack: String,
    onPlayPauseClick: () -> Unit,
    onNextTrackClick: () -> Unit,
    onPreviousTrackClick: () -> Unit,
    onAddToFavorites: () -> Unit,
    onSaveTrack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Reproduciendo: $currentTrack", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onAddToFavorites) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Añadir a Favoritos")
            }
            IconButton(onClick = onPreviousTrackClick) {
                Icon(Icons.Default.FastRewind, contentDescription = "Anterior Canción")
            }
            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir"
                )
            }
            IconButton(onClick = onNextTrackClick) {
                Icon(Icons.Default.FastForward, contentDescription = "Siguiente Canción")
            }
            IconButton(onClick = onSaveTrack) {
                Icon(Icons.Default.Save, contentDescription = "Guardar Canción")
            }

        }
    }
}
