package com.example.soundbeat_test.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.soundbeat_test.local.room.entities.PlaylistSong
import com.example.soundbeat_test.local.room.entities.Song

@Dao
interface PlaylistSongDao {

    /**
     * Inserta una relación entre una canción y una playlist.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playlistSong: PlaylistSong)

    /**
     * Elimina la relación entre una canción y una playlist.
     */
    @Delete
    suspend fun delete(playlistSong: PlaylistSong)

    /**
     * Devuelve todas las canciones de una playlist específica.
     */
    @Query(
        """
        SELECT S.* FROM Songs S
        INNER JOIN Playlist_Songs PS ON S.song_id = PS.song_id
        WHERE PS.playlist_id = :playlistId
        ORDER BY S.created_at DESC
    """
    )
    suspend fun getSongsForPlaylist(playlistId: Int): List<Song>

    /**
     * Elimina todas las relaciones de canciones para una playlist dada.
     */
    @Query("DELETE FROM Playlist_Songs WHERE playlist_id = :playlistId")
    suspend fun deleteAllSongsFromPlaylist(playlistId: Int)
}