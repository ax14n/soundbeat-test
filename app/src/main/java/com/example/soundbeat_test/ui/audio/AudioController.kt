package com.example.soundbeat_test.ui.audio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.soundbeat_test.ui.components.PlayerControls

/**
 * Composable que implementa un BottomSheet con controles de reproducción de música.
 *
 * @param audioPlayerViewModel ViewModel encargado de la reproducción de canciones.
 * @param content Contenido principal que se muestra detrás del reproductor.
 */
@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerDropdownMenu(
    audioPlayerViewModel: AudioPlayerViewModel, content: @Composable () -> Unit
) {

    val isPlaying by audioPlayerViewModel.isPlaying.collectAsState()
    val mediaItem by audioPlayerViewModel.currentMediaItem.collectAsState()
    val songName = mediaItem?.mediaMetadata?.title ?: "No title"
    val author = mediaItem?.mediaMetadata?.artist ?: "No author"

    val expanded = audioPlayerViewModel.reproducerIsShowing.collectAsState().value

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = slideInVertically { -it },
            exit = slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface, tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    MusicPlayerControls(
                        isPlaying = isPlaying,
                        currentTrack = songName.toString(),
                        author = author.toString(),
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
                        })
                }
            }
        }
        IconButton(
            onClick = { audioPlayerViewModel.togglePlayerVisibility() },
            modifier = Modifier
                .background(Color(0xFF026374))
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (expanded) "Cerrar controles" else "Abrir controles",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        content()

    }
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
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MusicPlayerControls(
    isPlaying: Boolean,
    currentTrack: String,
    author: String,
    onPlayPauseClick: () -> Unit,
    onNextTrackClick: () -> Unit,
    onPreviousTrackClick: () -> Unit,
    onAddToFavorites: () -> Unit,
    onSaveTrack: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Log.d("AudioController", "Canción: $currentTrack by $author")
        PlayerControls(
            songName = currentTrack,
            author = author,
            isPlaying = isPlaying,
            onPlayPauseClick = onPlayPauseClick,
            onNextTrackClick = onNextTrackClick,
            onPreviousTrackClick = onPreviousTrackClick,
            onAddToFavorites = onAddToFavorites,
            onSaveTrack = onSaveTrack
        )
    }
}
