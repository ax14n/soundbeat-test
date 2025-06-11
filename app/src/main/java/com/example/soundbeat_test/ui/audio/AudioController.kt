package com.example.soundbeat_test.ui.audio

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE) }
    val email = remember { mutableStateOf(prefs.getString("email", "OFFLINE")) }

    val isPlaying by audioPlayerViewModel.isPlaying.collectAsState()
    val currentMediaItem by audioPlayerViewModel.currentMediaItem.collectAsState()
    val currentMediaItemIndex by audioPlayerViewModel.currentIndex.collectAsState()
    val playlistLastIndex by audioPlayerViewModel.lastIndex.collectAsState()
    val nextMediaItem by audioPlayerViewModel.nextMediaItem.collectAsState()
    val isMarkedAsFavorite by audioPlayerViewModel.isMarkedAsFavorite.collectAsState()
    val isLocal by audioPlayerViewModel.isLocal.collectAsState()

    val songName = currentMediaItem?.mediaMetadata?.title ?: "No title"
    val nextName = nextMediaItem?.mediaMetadata?.title ?: "No title"
    val author = currentMediaItem?.mediaMetadata?.artist ?: "No author"

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
            }) {
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
                        nextTrack = nextName.toString(),
                        author = author.toString(),
                        index = currentMediaItemIndex,
                        len = playlistLastIndex
                    )
                }
            }
        }
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    email.value = prefs.getString("email", "OFFLINE")

                    if (email.value == "OFFLINE") {
                        Toast.makeText(
                            context, "You're in OFFLINE MODE", Toast.LENGTH_SHORT
                        ).show()
                    } else if (isLocal) {
                        Toast.makeText(
                            context, "Local songs can't be added to favorites.", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        audioPlayerViewModel.alternateFavoriteSong()
                    }


                }) {
                    val icon: ImageVector =
                        if (isMarkedAsFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                    Icon(icon, contentDescription = "Añadir a Favoritos")
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
    isPlaying: Boolean,
    currentTrack: String,
    nextTrack: String,
    author: String,
    index: Int,
    len: Int
) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Log.d("AudioController", "Canción: $currentTrack by $author")
        PlayerControls(
            currentTrack = currentTrack,
            author = author,
            nextTrack = nextTrack,
            index = index,
            len = len
        )
    }
}
