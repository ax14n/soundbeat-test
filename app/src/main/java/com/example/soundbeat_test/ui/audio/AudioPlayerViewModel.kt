package com.example.soundbeat_test.ui.audio

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.network.URL_BASE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.net.URLEncoder

/**
 * ViewModel que maneja la lógica de reproducción de audio usando ExoPlayer.
 *
 * Gestiona el estado de reproducción, como si está sonando o cuál es la canción actual.
 * Permite controlar la reproducción, avanzar, retroceder y saltar canciones.
 */
class AudioPlayerViewModel(
    application: Application
) : AndroidViewModel(application) {

    /**
     * ExoPlayer requiere un `Context` para su inicialización. En lugar de usar el
     * `ViewModel` estándar, que no proporciona acceso al `Context`, se utiliza
     * `AndroidViewModel(application)`. Esto permite acceder al `Context` a través
     * del método `getApplication()`, necesario para construir y configurar el ExoPlayer.
     */
    private val context = getApplication<Application>().applicationContext

    /**
     * ExoPlayer utilizado para la reproducción de audio.
     */
    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    /**
     * Estado que indica si el audio está siendo reproducido.
     */
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    /**
     * Estado que mantiene la canción que se está reproduciendo actualmente.
     */
    private val _currentMediaItem = MutableStateFlow<MediaItem?>(null)
    val currentMediaItem: StateFlow<MediaItem?> = _currentMediaItem

    /**
     * Listener para el ExoPlayer, actualiza los estados internos cuando cambia el estado de reproducción
     * o se cambia la canción.
     */
    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _currentMediaItem.value = mediaItem
        }
    }

    init {
        exoPlayer.addListener(listener)
    }

    /**
     * Carga y reproduce una transmisión de audio en formato HLS.
     *
     * @param url URL de la transmisión HLS a reproducir.
     */
    @OptIn(UnstableApi::class)
    fun loadAndPlayHLS(url: String) {
        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.volume = 1.0f // Volumen máximo
    }

    fun createSongUrl(album: Album): String {
        val url = "$URL_BASE/media/${
            URLEncoder.encode(
                album.name.trim() + ".m3u8", "UTF-8"
            )
        }"
        return url
    }

    /**
     * Pausa o reanuda la reproducción del audio actual.
     */
    fun playPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
    }

    /**
     * Salta a la siguiente canción, si la reproducción es parte de una lista de reproducción.
     */
    fun skipToNext() {
        exoPlayer.seekToNext()
    }

    /**
     * Retrocede a la canción anterior en la lista de reproducción.
     */
    fun skipToPrevious() {
        exoPlayer.seekToPrevious()
    }

    /**
     * Mueve la reproducción a una posición específica dentro de la canción.
     *
     * @param positionMs Posición en milisegundos a la que se debe mover la reproducción.
     */
    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
    }

    /**
     * Agrega la pista actual a la lista de favoritos del usuario.
     *
     * Esta función debe implementar la lógica necesaria para marcar una canción
     * como favorita, lo cual puede implicar persistencia en base de datos local
     * o sincronización con un servidor.
     */
    fun addToFavorites() {
        // TODO("Not implemented yet.")
    }

    /**
     * Guarda la pista actual para su uso offline o posterior reproducción.
     *
     * Esta función debería implementar la lógica para almacenar una canción
     * en almacenamiento local o en una base de datos interna, dependiendo de los
     * requisitos de la aplicación.
     */
    fun saveTrack() {
        // TODO("Not implemented yet.")
    }


    /**
     * Libera los recursos del ExoPlayer y elimina el listener cuando el ViewModel es destruido.
     */
    override fun onCleared() {
        super.onCleared()
        exoPlayer.removeListener(listener)
        exoPlayer.release()
    }
}
