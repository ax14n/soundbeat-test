package com.example.soundbeat_test.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa una playlist o lista de reproducción.
 *
 * Esta entidad mapea a la tabla "Playlists" y permite organizar
 * canciones bajo un nombre común.
 *
 * @property playlistId ID único de la playlist, generado automáticamente.
 * @property name Nombre de la playlist. No puede ser nulo.
 * @property createdAt Marca temporal que indica cuándo se creó la playlist.
 */
@Entity(tableName = "Playlists")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    val playlistId: Int = 0,

    val name: String,

    @ColumnInfo(name = "created_at")

    val createdAt: Long = System.currentTimeMillis()
)