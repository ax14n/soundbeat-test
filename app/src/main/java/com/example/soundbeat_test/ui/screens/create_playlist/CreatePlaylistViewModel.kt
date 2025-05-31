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
import androidx.room.Room
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.local.room.AppDatabase
import com.example.soundbeat_test.local.room.entities.Playlist
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

    val db = Room.databaseBuilder(
        context = application.applicationContext,
        AppDatabase::class.java,
        "nombre_de_tu_base_de_datos"
    ).build()

    private val _playlistName = mutableStateOf("Playlist nº1")
    val playlistName: State<String> = _playlistName

    private val _songs = MutableStateFlow<Set<Album>>(emptySet())
    val songs: StateFlow<Set<Album>> = _songs

    fun onPlaylistNameChange(newName: String) {
        _playlistName.value = newName
    }

    fun addSong(album: Album) {
        _songs.value = _songs.value + album
    }

    fun removeSong(album: Album) {
        _songs.value = _songs.value - album
    }

    fun clearSongsList() {
        _songs.value = emptySet()
    }

    fun clearPlaylistName() {
        _playlistName.value = ""
    }

    @OptIn(UnstableApi::class)
    fun createPlaylist(creationMode: CreationMode) {
        when (creationMode) {
            CreationMode.ONLINE_PLAYLIST -> {

                viewModelScope.launch {
                    val sharedPreferences = getApplication<Application>().getSharedPreferences(
                        "UserInfo",
                        Context.MODE_PRIVATE
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
                        // Maneja el caso en que no hay email almacenado
                        Log.e("createPlaylist", "No se encontró el email en SharedPreferences")
                    }
                }
            }

            CreationMode.OFFLINE_PLAYLIST -> {
                viewModelScope.launch {
                    val playlist = Playlist(
                        name = playlistName.value, createdAt = System.currentTimeMillis()
                    )
                    db.playlistDao()?.insert(playlist)
                    db.playlistDao()?.getAllPlaylists()?.last()?.playlistId
                    for (song in _songs.value) {

                    }
                }
            }
        }
    }


}
