package com.example.soundbeat_test.ui.screens.create_playlist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.soundbeat_test.data.Album

class CreatePlaylistViewModel : ViewModel() {

    private val _playlistName = mutableStateOf("Playlist nº1")
    val playlistName: State<String> = _playlistName

    private val _songs = mutableStateOf<Set<Album>>(emptySet())
    val songs: State<Set<Album>> = _songs

    fun onPlaylistNameChange(newName: String) {
        _playlistName.value = newName
    }

    fun addSong(album: Album) {
        _songs.value = _songs.value + album
    }

    fun removeSong(album: Album) {
        _songs.value = _songs.value - album
    }

    fun createPlaylist() {
        val name = _playlistName.value
        println("Creando playlist: $name")
    }

    fun discardPlaylist() {
        println("Playlist descartada")
    }
}
