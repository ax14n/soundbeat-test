package com.example.soundbeat_test.ui.screens.playlists

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.data.Playlist
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

    private val _userPlaylists = MutableStateFlow<List<Playlist>>(emptyList())
    val userPlaylists: StateFlow<List<Playlist>> = _userPlaylists

    private val _songs = MutableStateFlow<List<Album>>(emptyList<Album>())
    val songs: StateFlow<List<Album>> = _songs

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        obtainUserPlaylists()
    }

    /**
     * Llama a la API para obtener las playlists del usuario actualmente autenticado.
     * El resultado se almacena en [_userPlaylists] si es exitoso,
     * o en [_error] si ocurre algún fallo.
     */
    fun obtainUserPlaylists() {
        val savedEmail = getSavedEmail()

        if (savedEmail != null) {
            viewModelScope.launch {
                val result = getUserPlaylists(savedEmail)

                if (result.isSuccess) {
                    val playlists = result.getOrNull()
                    _userPlaylists.value = playlists ?: emptyList()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _error.value = error
                }
            }
        } else {
            _error.value = "No se encontró el email del usuario."
        }
    }

    /**
     * Llama a la API para obtener las canciones asociadas a una playlist específica.
     * El resultado se almacena en [_songs] si es exitoso,
     * o en [_error] si ocurre algún fallo.
     *
     * @param playlistId ID de la playlist cuyas canciones se desean obtener.
     */
    fun obtainPlaylistSongs(playlistId: Int) {
        viewModelScope.launch {
            val result = getPlaylistSongs(playlistId)
            if (result.isSuccess) {
                _songs.value = result.getOrDefault(emptyList())
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Error desconocido"
            }
        }
    }

    /**
     * Recupera el correo electrónico almacenado en `SharedPreferences`.
     *
     * @return El correo electrónico del usuario o `null` si no existe.
     */
    private fun getSavedEmail(): String? {
        val prefs = getApplication<Application>()
            .getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        return prefs.getString("email", null)
    }
}