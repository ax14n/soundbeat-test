package com.example.soundbeat_test.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.soundbeat_test.local.room.entities.Playlist

@Dao
interface PlaylistDao {

    /**
     * Inserta una nueva playlist.
     */
    @Insert
    suspend fun insert(playlist: Playlist): Long

    /**
     * Elimina una playlist (y todas sus relaciones por cascada).
     */
    @Delete
    suspend fun delete(playlist: Playlist)

    /**
     * Devuelve todas las playlists ordenadas por fecha de creación descendente.
     */
    @Query("SELECT * FROM Playlists ORDER BY created_at DESC")
    suspend fun getAllPlaylists(): List<Playlist>

    /**
     * Busca una playlist por nombre exacto.
     */
    @Query("SELECT * FROM Playlists WHERE name = :playlistName")
    suspend fun findByName(playlistName: String): Playlist?
}