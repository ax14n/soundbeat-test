package com.example.soundbeat_test.data

import com.example.soundbeat_test.R

/**
 * Representa un álbum de música con su nombre, autor, género y una imagen asociada.
 *
 * Esta clase se utiliza para almacenar la información de un álbum, incluyendo el nombre de la canción,
 * el autor, el género y la imagen de la portada. Además, la clase incluye un objeto compañero que proporciona
 * una lista de álbumes de ejemplo.
 *
 * @property id El identificador del album.
 * @property name El nombre de la canción en el álbum.
 * @property author El autor o banda que interpreta la canción.
 * @property genre El género musical de la canción.
 * @property imageResId El recurso de la imagen de la portada del álbum. El valor por defecto es `R.drawable.portada1`.
 */
data class Album(
    val id: Int = -1,
    val name: String = "Unknown",
    val author: String = "Unknown",
    val genre: List<String> = listOf<String>(),
    val imageResId: Int = R.drawable.default_vinyl,
    val url: String = "assign url",
    val duration: Double = 0.0
) {
    companion object {
        val AlbumExample = Album(
            id = 1,
            name = "Dark Side of the Moon",
            author = "Pink Floyd",
            genre = listOf("Progressive Rock", "Psychedelic Rock"),
            imageResId = R.drawable.premium_vinyl,
            url = "https://example.com/audio/dark_side.mp3",
            duration = 2580.0
        )
    }
}
