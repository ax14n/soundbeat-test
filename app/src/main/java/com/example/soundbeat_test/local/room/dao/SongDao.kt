package com.example.soundbeat_test.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.soundbeat_test.local.room.entities.Song

@Dao
interface SongDao {

    /**
     * Inserta una canción en la base de datos.
     */
    @Insert
    suspend fun insert(song: Song): Long

    /**
     * Elimina una canción específica.
     */
    @Delete
    suspend fun delete(song: Song)

    /**
     * Obtiene todas las canciones ordenadas por fecha de creación descendente.
     */
    @Query("SELECT * FROM Songs ORDER BY created_at DESC")
    suspend fun getAllSongs(): List<Song>

    /**
     * Busca canciones por artista.
     */
    @Query("SELECT * FROM Songs WHERE artist = :artistName")
    suspend fun findByArtist(artistName: String): List<Song>

    @Query("SELECT * FROM Songs WHERE title = :title AND artist = :artist LIMIT 1")
    suspend fun getSongByTitleAndArtist(title: String, artist: String): Song?
}
