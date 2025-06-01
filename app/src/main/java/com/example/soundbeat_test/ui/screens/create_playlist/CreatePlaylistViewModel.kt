package com.example.soundbeat_test.ui.screens.create_playlist

import android.app.Application
import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.data.Album.Companion.toSong
import com.example.soundbeat_test.local.room.DatabaseProvider
import com.example.soundbeat_test.local.room.entities.Playlist
import com.example.soundbeat_test.local.room.entities.PlaylistSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Modos internos que usa el ViewModel para identificar si se debe crear una playlist localmente
 * o remotamente.
 */
enum class CreationMode {
    OFFLINE_PLAYLIST, ONLINE_PLAYLIST
}

class CreatePlaylistViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Instancia de la base de datos. Necesaria para agregar y crear Playlists localmente.
     */
    private val db = DatabaseProvider.getDatabase(application.applicationContext)
    private val localPlaylistDb = db.playlistDao()
    private val localSongDb = db.songDao()
    private val localPlaylistSongDb = db.playlistSongDao()

    private val _playlistName = mutableStateOf("Playlist nº1")
    val playlistName: State<String> = _playlistName

    private val _songs = MutableStateFlow<Set<Album>>(emptySet())
    val songs: StateFlow<Set<Album>> = _songs

    /**
     * Actualiza el texto conforme a lo que el usuario esté escribiendo en tiempo real.
     */
    fun onPlaylistNameChange(newName: String) {
        _playlistName.value = newName
    }

    /**
     * Agrega una canción a la lista de canciones.
     */
    fun addSong(album: Album) {
        _songs.value = _songs.value + album
    }

    /**
     * Remueve una canción almacenada y seleccionada dentro de la lista de canciones.
     */
    fun removeSong(album: Album) {
        _songs.value = _songs.value - album
    }

    /**
     * Limpia la lista.
     */
    fun clearSongsList() {
        _songs.value = emptySet()
    }

    /**
     * Limpia el nombre de la playlist.
     */
    fun clearPlaylistName() {
        _playlistName.value = ""
    }

    /**
     * Crea una Playlist remota o local definida por parámetro.
     * @param creationMode: Modo de creación de la Playlist.
     * @see CreationMode
     */
    @OptIn(UnstableApi::class)
    fun createPlaylist(creationMode: CreationMode) {
        when (creationMode) {
            CreationMode.ONLINE_PLAYLIST -> {
                createRemotePlaylist()
            }

            CreationMode.OFFLINE_PLAYLIST -> {
                createLocalPlaylist()
            }
        }
    }

    /**
     * Contacta con la base de datos del servidor y crea una Playlist con las canciones
     * seleccionadas por el usuario. Las canciones seleccionadas son obtenidas del servidor.
     */
    @OptIn(UnstableApi::class)
    private fun createRemotePlaylist() {
        viewModelScope.launch {
            val sharedPreferences = getApplication<Application>().getSharedPreferences(
                "UserInfo", Context.MODE_PRIVATE
            )
            val userEmail = sharedPreferences.getString("email", null)

            if (userEmail != null) {
                val ids = _songs.value.toList().map { it.id }
                com.example.soundbeat_test.network.createPlaylist(
                    playlistName = playlistName.value,
                    userEmail = userEmail,
                    songsId = ids,
                )
            } else {
                Log.e("createPlaylist", "No se encontró el email en SharedPreferences")
            }
        }
    }

    /**
     * Contacta con la base de datos local del sistema y crea una Playlist con las canciones
     * seleccionadas. Para que las canciones puedan ser agregadas a la playlist, se obtiene
     * la información de las canciones y se agregan también a la base de datos.
     */
    private fun createLocalPlaylist() {
        viewModelScope.launch {
            val playlist = Playlist(
                name = playlistName.value, createdAt = System.currentTimeMillis()
            )
            localPlaylistDb?.insert(playlist)
            val playlistId = localPlaylistDb?.getAllPlaylists()?.last()?.playlistId
            for (song in _songs.value) {
                localSongDb?.insert(song.toSong())
                val songId = localSongDb?.getAllSongs()?.last()?.songId
                var playlistSong = PlaylistSong(
                    playlist_id = playlistId!!, song_id = songId!!
                )
                localPlaylistSongDb?.insert(playlistSong)
            }
        }
    }

}
