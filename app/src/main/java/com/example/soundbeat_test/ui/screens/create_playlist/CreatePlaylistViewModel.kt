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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(application: Application) : AndroidViewModel(application) {

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
    fun createPlaylist() {
        viewModelScope.launch {
            val sharedPreferences = getApplication<Application>()
                .getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
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


}
