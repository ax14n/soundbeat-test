package com.example.soundbeat_test.data

import com.example.soundbeat_test.R

/**
 * Representa una playlist dentro de la aplicación.
 *
 * @property id El identificador de la playlist.
 * @property name El nombre de la playlist.
 * @property songs Un conjunto de objetos [Album] que representan las canciones incluidas en la playlist.
 */
data class Playlist(
    val id: Int = -1,
    val name: String = "assign name",
    var songs: Set<Album> = emptySet<Album>()
) {
    companion object {
        val PlaylistExample = Playlist(
            id = 101,
            name = "Classic Rock",
            songs = setOf(
                Album.AlbumExample,
                Album(
                    id = 2,
                    name = "Led Zeppelin IV",
                    author = "Led Zeppelin",
                    genre = listOf("Hard Rock", "Blues Rock"),
                    imageResId = R.drawable.premium_vinyl,
                    url = "https://example.com/audio/led_zeppelin_iv.mp3",
                    duration = 2550.0
                ),
                Album.AlbumExample,
            )
        )

        fun Playlist.toEntity(): com.example.soundbeat_test.local.room.entities.Playlist {
            return com.example.soundbeat_test.local.room.entities.Playlist(
                playlistId = this.id,
                name = this.name,
                createdAt = 0
            )
        }

    }
}
