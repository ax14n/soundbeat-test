package com.example.soundbeat_test.local.room.entities

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * Relación muchos a muchos entre canciones y playlists.
 *
 * Esta entidad mapea a la tabla "Playlist_Songs", que asocia canciones
 * con playlists a través de claves foráneas. Permite que una canción
 * pertenezca a múltiples playlists y viceversa.
 *
 * @property playlist_id ID de la playlist a la que pertenece la canción.
 * @property song_id ID de la canción que está en la playlist.
 */
@Entity(
    tableName = "Playlist_Songs",
    primaryKeys = ["playlist_id", "song_id"],
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["playlist_id"],
            childColumns = ["playlist_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Song::class,
            parentColumns = ["song_id"],
            childColumns = ["song_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistSong(
    val playlist_id: Int,
    val song_id: Int
)