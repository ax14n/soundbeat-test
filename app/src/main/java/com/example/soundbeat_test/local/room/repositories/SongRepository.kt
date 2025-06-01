package com.example.soundbeat_test.local.room.repositories

import com.example.soundbeat_test.local.room.dao.SongDao
import com.example.soundbeat_test.local.room.entities.Song

/**
 * Repositorio para el acceso a datos de canciones.
 *
 * Proporciona métodos para insertar nuevas canciones, eliminarlas,
 * recuperar todas las canciones ordenadas por fecha de creación y buscar canciones por artista.
 * Facilita la comunicación entre el ViewModel y el DAO sin exponer directamente operaciones de base de datos.
 */
class SongRepository(private val songDao: SongDao) {

    suspend fun insert(song: Song) {
        songDao.insert(song)
    }

    suspend fun delete(song: Song) {
        songDao.delete(song)
    }

    suspend fun getAllSongs(): List<Song> {
        return songDao.getAllSongs()
    }

    suspend fun findSongsByArtist(artistName: String): List<Song> {
        return songDao.findByArtist(artistName)
    }
}
