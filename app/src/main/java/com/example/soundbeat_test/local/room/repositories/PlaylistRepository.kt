package com.example.soundbeat_test.local.room.repositories

import com.example.soundbeat_test.local.room.dao.PlaylistDao
import com.example.soundbeat_test.local.room.entities.Playlist

/**
 * Repositorio responsable de gestionar el acceso a datos relacionados con las playlists.
 *
 * Esta clase actúa como intermediario entre el ViewModel y el DAO de playlists,
 * proporcionando una capa de abstracción que facilita la gestión de las operaciones
 * de base de datos como inserciones, eliminaciones, consultas y búsquedas por nombre.
 */
class PlaylistRepository(private val playlistDao: PlaylistDao) {

    suspend fun insert(playlist: Playlist) {
        playlistDao.insert(playlist)
    }

    suspend fun delete(playlist: Playlist) {
        playlistDao.delete(playlist)
    }

    suspend fun getAllPlaylists(): List<Playlist> {
        return playlistDao.getAllPlaylists()
    }

    suspend fun findPlaylistByName(name: String): Playlist? {
        return playlistDao.findByName(name)
    }
}