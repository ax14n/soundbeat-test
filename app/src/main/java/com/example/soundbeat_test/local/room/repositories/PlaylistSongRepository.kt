package com.example.soundbeat_test.local.room.repositories

import com.example.soundbeat_test.local.room.dao.PlaylistSongDao
import com.example.soundbeat_test.local.room.entities.PlaylistSong
import com.example.soundbeat_test.local.room.entities.Song

/**
 * Repositorio encargado de manejar las relaciones entre canciones y playlists.
 *
 * Ofrece métodos para insertar y eliminar asociaciones entre canciones y playlists,
 * obtener todas las canciones de una playlist específica, y eliminar todas las canciones
 * asociadas a una playlist determinada. Centraliza la lógica relacionada con la tabla intermedia.
 */
class PlaylistSongRepository(private val playlistSongDao: PlaylistSongDao) {

    suspend fun addSongToPlaylist(playlistSong: PlaylistSong) {
        playlistSongDao.insert(playlistSong)
    }

    suspend fun removeSongFromPlaylist(playlistSong: PlaylistSong) {
        playlistSongDao.delete(playlistSong)
    }

    suspend fun getSongsInPlaylist(playlistId: Int): List<Song> {
        return playlistSongDao.getSongsForPlaylist(playlistId)
    }

    suspend fun clearPlaylist(playlistId: Int) {
        playlistSongDao.deleteAllSongsFromPlaylist(playlistId)
    }
}
