package com.example.soundbeat_test.ui.screens.playlists

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.data.Playlist
import com.example.soundbeat_test.local.room.DatabaseProvider.getPlaylistDao
import com.example.soundbeat_test.local.room.DatabaseProvider.getPlaylistSongDao
import com.example.soundbeat_test.network.getPlaylistSongs
import com.example.soundbeat_test.network.getUserPlaylists
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar los datos relacionados con las playlists y sus canciones
 * en la interfaz de usuario, utilizando corrutinas y StateFlow para mantener la reactividad.
 *
 * @param application La instancia de la aplicación utilizada para acceder al contexto si es necesario.
 */
class PlaylistScreenViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val context = getApplication<Application>()

    private val _remoteUserPlaylists = MutableStateFlow<List<Playlist>>(emptyList())
    val remoteUserPlaylists: StateFlow<List<Playlist>> = _remoteUserPlaylists

    private val _localUserPlaylist = MutableStateFlow<List<Playlist>>(emptyList())
    val localUserPlaylist: StateFlow<List<Playlist>> = _localUserPlaylist

    private val _songs = MutableStateFlow<List<Album>>(emptyList<Album>())
    val songs: StateFlow<List<Album>> = _songs

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        obtainRemoteUserPlaylists()
        obtainLocalUserPlaylists()
    }

    /**
     * Llama a la API para obtener las playlists del usuario actualmente autenticado.
     * El resultado se almacena en [_remoteUserPlaylists] si es exitoso,
     * o en [_error] si ocurre algún fallo.
     */
    fun obtainRemoteUserPlaylists() {
        val savedEmail = getSavedEmail()

        if (savedEmail != null) {
            viewModelScope.launch {
                val result = getUserPlaylists(savedEmail)

                if (result.isSuccess) {
                    val playlists = result.getOrNull()
                    _remoteUserPlaylists.value = playlists ?: emptyList()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _error.value = error
                }
            }
        } else {
            _error.value = "No se encontró el email del usuario."
            Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Obtiene y lista las canciones locales almacenadas dentro de la base de datos local del usuario.
     */
    fun obtainLocalUserPlaylists() {
        viewModelScope.launch {
            val playlistDao = getPlaylistDao(context)
            _localUserPlaylist.value = playlistDao.getAllPlaylists().map { playlist ->
                Playlist(
                    id = playlist.playlistId, name = playlist.name, songs = setOf<Album>()
                )
            }
        }
    }

    /**
     * Llama a la API para obtener las canciones asociadas a una playlist específica.
     * El resultado se almacena en [_songs] si es exitoso,
     * o en [_error] si ocurre algún fallo.
     *
     * @param playlistId ID de la playlist cuyas canciones se desean obtener.
     */
    fun obtainRemotePlaylistSongs(playlistId: Int) {
        viewModelScope.launch {
            val result = getPlaylistSongs(playlistId)
            if (result.isSuccess) {
                _songs.value = result.getOrDefault(emptyList())
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Error desconocido"
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun obtainLocalPlaylistSongs(playlistId: Int) {
        viewModelScope.launch {
            val songs = getPlaylistSongDao(
                context = context
            ).getSongsForPlaylist(playlistId).map { song ->
                Album(
                    id = song.songId,
                    name = song.title,
                    author = song.artist,
                    url = song.url,
                    duration = song.duration.toDouble(),
                    isLocal = true
                )
            }
            _songs.value = songs
        }
    }

    /**
     * Recupera el correo electrónico almacenado en `SharedPreferences`.
     *
     * @return El correo electrónico del usuario o `null` si no existe.
     */
    private fun getSavedEmail(): String? {
        val prefs = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        return prefs.getString("email", null)
    }
}