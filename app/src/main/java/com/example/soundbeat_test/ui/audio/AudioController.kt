package com.example.soundbeat_test.ui.audio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.ui.input.pointer.pointerInput
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
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        if (dragAmount > 0) {
                            audioPlayerViewModel.showPlayerVisibility()
                        } else {
                            audioPlayerViewModel.hidePlayerVisibility()
                        }
                    })
            }
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MusicPlayerControls(
                        isPlaying = isPlaying,
                        currentTrack = songName.toString(),
                        author = author.toString()
                    )
                }
            }
        }
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF026374))
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { audioPlayerViewModel.addToFavorites() }) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Añadir a Favoritos")
                }
                IconButton(onClick = { audioPlayerViewModel.skipToPrevious() }) {
                    Icon(Icons.Default.FastRewind, contentDescription = "Anterior Canción")
                }
                IconButton(onClick = { audioPlayerViewModel.playPause() }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproducir"
                    )
                }
                IconButton(onClick = { audioPlayerViewModel.skipToNext() }) {
                    Icon(Icons.Default.FastForward, contentDescription = "Siguiente Canción")
                }
                IconButton(onClick = { audioPlayerViewModel.saveTrack() }) {
                    Icon(Icons.Default.Save, contentDescription = "Guardar Canción")
                }
            }
        }
        Box() {
            content()
//            Spacer(modifier = Modifier.padding(vertical = (10.dp)))
//            IconButton(
//                modifier = Modifier
//                    .align(alignment = Alignment.TopCenter)
//                    .offset(y = -(15.dp))
//                    .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
//                    .background(Color(0xFF026374)),
//                onClick = { audioPlayerViewModel.togglePlayerVisibility() },
//            ) {
//                Icon(
//                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                    contentDescription = if (expanded) "Cerrar controles" else "Abrir controles",
//                    tint = Color.Black,
//                    modifier = Modifier.size(23.dp)
//                )
//            }
        }

    }
}


/**
 * Composable que muestra los controles de reproducción de música.
 *
 * @param isPlaying Estado que indica si hay reproducción activa.
 * @param currentTrack Nombre de la canción actual.
 */
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun MusicPlayerControls(
    isPlaying: Boolean, currentTrack: String, author: String
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Log.d("AudioController", "Canción: $currentTrack by $author")
        PlayerControls(
            songName = currentTrack, author = author
        )
    }
}
