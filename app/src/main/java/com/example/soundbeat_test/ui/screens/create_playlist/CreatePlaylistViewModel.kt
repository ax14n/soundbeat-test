package com.example.soundbeat_test.ui.screens.create_playlist

import android.app.Application
import android.content.Context
import androidx.annotation.OptIn
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.data.Album.Companion.toSong
import com.example.soundbeat_test.local.room.DatabaseProvider.getPlaylistDao
import com.example.soundbeat_test.local.room.DatabaseProvider.getPlaylistSongDao
import com.example.soundbeat_test.local.room.DatabaseProvider.getSongDao
import com.example.soundbeat_test.local.room.entities.Playlist
import com.example.soundbeat_test.local.room.entities.PlaylistSong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Modos internos que usa el ViewModel para identificar si se debe crear una playlist localmente
 * o remotamente.
 */
enum class PlaylistOrigin {
    OFFLINE_PLAYLIST, ONLINE_PLAYLIST
}

class CreatePlaylistViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Instancia de acceso a `PlaylistDao`.
     */
    private val localPlaylistDb = getPlaylistDao(application.applicationContext)

    /**
     * Instancia de acceso a `SongDao`.
     */
    private val localSongDb = getSongDao(application.applicationContext)

    /**
     * Instancia de acceso a `PlaylistSongDao`.
     */
    private val localPlaylistSongDb = getPlaylistSongDao(application.applicationContext)

    private val _playlistName = MutableStateFlow("Playlist nº1")
    val playlistName: StateFlow<String> = _playlistName

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
    @OptIn(UnstableApi::class)
    fun addSong(album: Album) {
        Log.d("PlaylistCreation", "adding $album")
        _songs.value = _songs.value + album
    }

    /**
     * Remueve una canción almacenada y seleccionada dentro de la lista de canciones.
     */
    @OptIn(UnstableApi::class)
    fun removeSong(album: Album) {
        Log.d("PlaylistCreation", "removing $album")
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
     * @param playlistOrigin: Modo de creación de la Playlist.
     * @see PlaylistOrigin
     */
    @OptIn(UnstableApi::class)
    fun createPlaylist(playlistOrigin: PlaylistOrigin) {
        Log.d("PlaylistCreation", "creating playlist. origin:  $playlistOrigin")
        when (playlistOrigin) {
            PlaylistOrigin.ONLINE_PLAYLIST -> {
                createRemotePlaylist()
            }

            PlaylistOrigin.OFFLINE_PLAYLIST -> {
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
            clearSongsList()
        }
    }

    /**
     * Contacta con la base de datos local del sistema y crea una Playlist con las canciones
     * seleccionadas. Para que las canciones puedan ser agregadas a la playlist, se obtiene
     * la información de las canciones y se agregan también a la base de datos.
     */
    @OptIn(UnstableApi::class)
    private fun createLocalPlaylist() {
        viewModelScope.launch {
            try {
                Log.d("PlaylistCreation", "Inicio de creación de playlist local")
                val playlist = Playlist(
                    name = playlistName.value,
                    createdAt = System.currentTimeMillis(),
                    playlistId = 0
                )

                Log.d("PlaylistCreation", "adding playlist: $playlist")
                val playlistId = localPlaylistDb.insert(playlist)?.toInt()

                Log.d("PlaylistCreation", "created playlist id: $playlistId")

                Log.d("PlaylistCreation", "songs list size to iterate: ${_songs.value.size}")
                for (album in _songs.value) {
                    Log.d("PlaylistCreation", "adding song: $album")
                    val existing = localSongDb.getSongByTitleAndArtist(album.title, album.author)

                    val songId = existing?.songId ?: localSongDb.insert(album.toSong()).toInt()

                    Log.d("PlaylistCreation", "inserted song id: $songId")

                    val playlistSong = PlaylistSong(
                        playlist_id = playlistId!!, song_id = songId
                    )
                    Log.d("PlaylistCreation", "inserting Playlist-Song relation: $playlistSong")

                    localPlaylistSongDb?.insert(playlistSong)
                }
                Log.d("PlaylistCreation", "Creación de playlist local finalizada correctamente")
                clearSongsList()
            } catch (e: Exception) {
                Log.e("PlaylistCreation", "Error al crear la playlist local", e)
            }
        }
    }


}
