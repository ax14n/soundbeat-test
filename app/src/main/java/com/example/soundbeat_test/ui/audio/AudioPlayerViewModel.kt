package com.example.soundbeat_test.ui.audio

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.network.ServerConfig
import com.example.soundbeat_test.network.addSongToFavorites
import com.example.soundbeat_test.network.isFavorite
import com.example.soundbeat_test.network.removeSongFromFavorites
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
     * ExoPlayer utilizado para la reproducción de audio.
     */
    private val exoPlayer: ExoPlayer =
        ExoPlayer.Builder(getApplication<Application>().applicationContext).build()

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

    private val _nextMediaItem = MutableStateFlow<MediaItem?>(null)
    val nextMediaItem: StateFlow<MediaItem?> = _nextMediaItem

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _lastIndex = MutableStateFlow(0)
    val lastIndex: StateFlow<Int> = _lastIndex

    private val _reproducerIsShowing = MutableStateFlow<Boolean>(false)
    val reproducerIsShowing: StateFlow<Boolean> = _reproducerIsShowing

    /**
     * Almacena si la canción que actualmente está sonando se encuentra en favoritos.
     */
    private val _isMarkedAsFavorite = MutableStateFlow<Boolean>(false)

    /**
     * Muestra si la canción que se encuentra actualmente sonando está en favoritos.
     */
    val isMarkedAsFavorite: StateFlow<Boolean> = _isMarkedAsFavorite

    /**
     * Lista de albumes temporales. Las canciones que son cargadas son almacenadas aquí para
     * facilitar la obtención de datos para agregaras a favoritos o a alguna playlist del usuario.
     */
    private val _temporalAlbumList = MutableStateFlow<Set<Album>>(emptySet())

    private val _isLocal = MutableStateFlow<Boolean>(false)
    val isLocal: StateFlow<Boolean> = _isLocal

    fun showPlayerVisibility() {
        _reproducerIsShowing.value = true
    }

    fun hidePlayerVisibility() {
        _reproducerIsShowing.value = false
    }

    /**
     * Listener para el ExoPlayer, actualiza los estados internos cuando cambia el estado de reproducción
     * o se cambia la canción.
     */
    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
        }

        @OptIn(UnstableApi::class)
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            _currentMediaItem.value = mediaItem
            _currentIndex.value = exoPlayer.currentMediaItemIndex
            Log.d("AudioPlayerViewModele", "Current index: ${_currentIndex.value}")

            viewModelScope.launch {
                val prefs =
                    getApplication<Application>().getSharedPreferences("UserInfo", MODE_PRIVATE)
                val email = prefs.getString("email", "ERROR")
                val result = isFavorite(
                    email = email!!,
                    album = _temporalAlbumList.value.toList()[_currentIndex.value]
                )
                _isMarkedAsFavorite.value = (result == "true")
            }

            val nextIndex = exoPlayer.currentMediaItemIndex + 1
            Log.d("AudioPlayerViewModele", "Next index: $nextIndex")
            val nextItem = exoPlayer.getMediaItemAt(nextIndex)
            _nextMediaItem.value = nextItem

        }

    }

    init {
        exoPlayer.addListener(listener)
    }

    /**
     * Carga y reproduce una transmisión de audio en formato HLS con metadatos.
     *
     * Esta función reemplaza cualquier contenido anterior del reproductor y comienza a reproducir
     * automáticamente desde el principio. Se utiliza `MediaItem.Builder` para asociar metadatos
     * como el título y el autor de la pista.
     *
     * @param url URL de la transmisión HLS a reproducir.
     * @param title Título de la canción (por ejemplo, el nombre del álbum).
     * @param artist (Opcional) Nombre del autor o artista.
     */
    @OptIn(UnstableApi::class)
    fun loadAndPlayHLS(album: Album) {
        _temporalAlbumList.value = setOf<Album>(album)
        _isLocal.value = album.isLocal
        Log.d("AudioPlayerViewModel", "url: $album.url")
        val mediaItem = MediaItem.Builder().setUri(album.url).setMediaMetadata(
            MediaMetadata.Builder().setTitle(album.title)
                .setArtist(album.title ?: "Autor desconocido").build()
        ).build()

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.volume = 1.0f // Volumen máximo
        exoPlayer.playWhenReady = true
    }

    /**
     * Genera la URL del recurso HLS asociado a un álbum.
     *
     * Si el álbum es remoto (no local), construye la URL correspondiente al archivo `.m3u8`
     * almacenado en el servidor, codificando el nombre del archivo en UTF-8 para asegurar
     * compatibilidad con la sintaxis de URLs. Si el álbum es local, simplemente devuelve la URI
     * del archivo local ya contenida en el objeto [Album].
     *
     * @param album Álbum que contiene los datos de la canción a reproducir.
     * @return Cadena de texto representando la URL (remota) o URI (local) del recurso de audio.
     */
    @OptIn(UnstableApi::class)
    fun createSongUrl(album: Album): String {
        val result = if (!album.isLocal) {
            val encodedName = URLEncoder.encode(album.title.trim() + ".m3u8", "UTF-8")
            val url = "${ServerConfig.getBaseUrl()}/media/$encodedName"
            return url
        } else {
            album.url
        }
        Log.d("AudioPlayerViewModel", "URL o URI: $result - isLocal: ${album.isLocal}")
        return result
    }

    /**
     * Carga y reproduce una lista de álbumes como una lista de reproducción (playlist).
     *
     * Convierte cada objeto [Album] en un [MediaItem] a partir de su URL, utilizando
     * [createSongUrl]. Luego establece la lista de medios en el ExoPlayer, la prepara
     * para reproducción y comienza a reproducir automáticamente desde la primera pista.
     *
     * Esta función reemplaza cualquier contenido previamente cargado en el reproductor,
     * y permite que los métodos [skipToNext] y [skipToPrevious] funcionen correctamente,
     * ya que ahora hay una cola de reproducción.
     *
     * @param albums Lista de álbumes que representan la lista de reproducción a cargar.
     */
    @OptIn(UnstableApi::class)
    fun loadPlaylist(albums: List<Album>) {
        _temporalAlbumList.value = albums.toSet()
        _isLocal.value = albums[0].isLocal

        val mediaItems = albums.map { album ->
            val uri = createSongUrl(album)
            Log.d("AudioPlayerViewModel", "${album.title} : $uri")

            MediaItem.Builder().setUri(uri).setMediaMetadata(
                MediaMetadata.Builder().setTitle(album.title).setArtist(
                    album.author
                ).build()
            ).build()
        }

        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
        _lastIndex.value = albums.size
        exoPlayer.playWhenReady = true
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
    private fun addToFavorites() {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("UserInfo", MODE_PRIVATE)
            val email = prefs.getString("email", "ERROR")
            if (email != "OFFLINE" && email != "ERROR") {
                val album = _temporalAlbumList.value.toList()[_currentIndex.value]
                addSongToFavorites(
                    email = email!!, album = album
                )
                _isMarkedAsFavorite.value = true
            }
        }
    }

    /**
     * Elimina una cancion de favoritos.
     */
    private fun removeFromFavorites() {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("UserInfo", MODE_PRIVATE)
            val email = prefs.getString("email", "ERROR")
            if (email != "OFFLINE" && email != "ERROR") {
                val album = _temporalAlbumList.value.toList()[_currentIndex.value]
                removeSongFromFavorites(
                    email = email!!, album = album
                )
                _isMarkedAsFavorite.value = false
            }
        }
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
     * Cambia el estado a su estado contrario para indicar si a una persona le gusta una canción o
     * deja de gustarle. Las canciones favoritas son almacenadas en una tabla aparte a las playlists.
     */
    @OptIn(UnstableApi::class)
    fun alternateFavoriteSong() {
        _isMarkedAsFavorite.value = !_isMarkedAsFavorite.value
        if (_isMarkedAsFavorite.value) addToFavorites() else removeFromFavorites()
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
