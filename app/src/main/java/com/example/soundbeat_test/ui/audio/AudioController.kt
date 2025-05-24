package com.example.soundbeat_test.ui.audio

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.soundbeat_test.ui.components.PlayerControls
import com.example.soundbeat_test.ui.screens.selected_playlist.SharedPlaylistViewModel

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
    val mediaItem by audioPlayerViewModel.currentMediaItem.collectAsState()
    val songName = mediaItem?.mediaMetadata?.title ?: "No title"
    val author = mediaItem?.mediaMetadata?.artist ?: "No author"

    BottomSheetScaffold(scaffoldState = bottomSheetState, sheetContent = {
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
    }, sheetPeekHeight = 56.dp, content = {
        content()
    })
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
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
