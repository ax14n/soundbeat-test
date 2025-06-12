package com.example.soundbeat_test.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.soundbeat_test.R
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.data.Playlist

/**
 * Muestra una lista horizontal de elementos que pueden ser de tipo Album o Playlist.
 * Cada elemento se renderiza según su tipo usando un LazyRow.
 *
 * @param list Lista de elementos heterogéneos (Album o Playlist).
 */
@Preview
@Composable
fun AlbumHorizontalList(
    list: List<Any> = listOf<Playlist>(
        Playlist.PlaylistExample, Playlist.PlaylistExample, Playlist.PlaylistExample
    ), onPressedCover: (Any) -> Unit = {}
) {
    LazyRow {
        items(
            items = list, key = { item ->
                when (item) {
                    is Album -> "album_${item.id}"
                    is Playlist -> "playlist_${item.id}"
                    else -> item.hashCode()
                }
            }) { item ->
            when (item) {
                is Album -> {
                    AlbumItem(album = item, onPressedCover = { onPressedCover(item) })
                }

                is Playlist -> {
                    PlaylistItem(playlist = item, onPressedCover = { onPressedCover(item) })
                }
            }
        }
    }
}

/**
 * Tarjeta de presentación de un álbum con portada, título, autor y géneros.
 *
 * @param album Álbum a mostrar.
 * @param onClickedAlbumCover Callback cuando se pulsa la portada del álbum.
 */
@Preview
@Composable
fun AlbumCard(
    album: Album = Album.AlbumExample,
    modifier: Modifier = Modifier,
    onClickedAlbumCover: () -> Unit = {}
) {
    var isMarqueeOn by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumCover() {
            onClickedAlbumCover()
        }
        Spacer(modifier = Modifier.width(12.dp))


        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { isMarqueeOn = !isMarqueeOn }) {
            Text(
                modifier = if (isMarqueeOn) Modifier.basicMarquee() else Modifier,
                text = album.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1
            )
            Text(
                text = "by ${album.author}",
                fontSize = 13.sp,
                color = Color.DarkGray,
                modifier = if (isMarqueeOn) Modifier.basicMarquee() else Modifier
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(album.genre.take(3)) { tag ->
                    Text(
                        text = tag,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color(0xFF7DA1C5), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PlayerControls(
    currentTrack: String = "Unknown",
    author: String = "Unknown",
    nextTrack: String = "Unknown",
    index: Int = 1,
    len: Int = index
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        AlbumCover()
        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = currentTrack,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                modifier = Modifier.basicMarquee()

            )
            Text(
                text = "by $author",
                fontSize = 13.sp,
                color = Color.DarkGray,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .basicMarquee()

            )

            Text(
                text = "next track: ${if (index + 1 == len) "no more tracks" else nextTrack}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                modifier = Modifier.basicMarquee()
            )
        }
    }
}

/**
 * Ítem compacto de álbum para usar en listas horizontales o vistas resumidas.
 *
 * @param album Álbum que se va a mostrar.
 * @param onPressedCover Acción al hacer clic sobre el ítem.
 */
@Preview
@Composable
fun AlbumItem(album: Album = Album.AlbumExample, onPressedCover: (Album) -> Unit = {}) {
    Column(modifier = Modifier) {
        AlbumCover() { onPressedCover(album) }
        Text(text = album.title.take(17), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(text = "by ${album.author}".take(17), maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * Ítem compacto de playlist con portada, nombre e ID.
 *
 * @param playlist Playlist que se va a mostrar.
 * @param onPressedCover Acción al hacer clic sobre el ítem.
 */
@Preview
@Composable
@Preview
fun PlaylistItem(
    playlist: Playlist = Playlist.PlaylistExample, onPressedCover: (Playlist) -> Unit = {}
) {
    Column(modifier = Modifier) {
        PlaylistCover() { onPressedCover(playlist) }
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = playlist.name.take(17), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(
            text = "id = ${playlist.id}".take(17), maxLines = 1, overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Portada de álbum compuesta por una imagen de disco y una portada superpuesta.
 *
 * @param cover Imagen de la portada del álbum.
 * @param disk Imagen del disco de vinilo.
 * @param onPressedCover Acción al hacer clic sobre la portada.
 */
@Preview()
@Composable
fun AlbumCover(
    cover: Int = R.drawable.premium_vinyl,
    disk: Int = R.drawable.vinyl,
    size: Int = 1,
    onPressedCover: () -> Unit = {}
) {
    Row(
        Modifier.padding(end = 50.dp)
    ) {

        Box(contentAlignment = Alignment.Center) {
            val diskPainter = rememberAsyncImagePainter(model = disk) // o cover
            val coverPainter = rememberAsyncImagePainter(model = cover) // o cover
            Image(
                painter = diskPainter,
                contentDescription = "Disco",
                modifier = Modifier
                    .offset(x = 50.dp * size)
                    .size(90.dp * size),
            )

            Image(
                painter = coverPainter,
                contentDescription = "Portada del álbum",
                modifier = Modifier
                    .size(100.dp * size)
                    .clickable(onClick = { onPressedCover() })

            )

        }
    }

}

/**
 * Portada de playlist formada por dos imágenes superpuestas, una rotada detrás.
 *
 * @param onPressedCover Acción al hacer clic sobre la portada.
 */
@Preview()
@Composable
fun PlaylistCover(
    onPressedCover: () -> Unit = {}
) {
    Row(
        Modifier.padding(end = 50.dp)
    ) {
        Box(
            modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.default_vinyl),
                contentDescription = "Album cover background",
                modifier = Modifier
                    .size(100.dp)
                    .rotate(-20f)
                    .offset(x = 15.dp, y = (5).dp)
            )

            Image(
                painter = painterResource(id = R.drawable.premium_vinyl),
                contentDescription = "Album cover front",
                modifier = Modifier
                    .size(100.dp)
                    .clickable(onClick = { onPressedCover() })
            )
        }
    }

}

@Preview
@Composable
fun FavoritePlaylist(
    onPressedCover: () -> Unit = {}
) {
    Box {
        AlbumCover(size = 2) {
            onPressedCover
        }
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.Red,
            modifier = Modifier
                .size(100.dp * 2)
                .offset(x = 2.dp * 2, y = 0.dp)
        )
    }
}

/**
 * Imagen de usuario compuesta por un vinilo grande y el centro del vinilo superpuesto.
 */
@Preview
@Composable
fun UserImage() {
    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.premium_vinyl),
            contentDescription = "Vinyl",
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(1000.dp))
                .shadow(50.dp)
        )
        Image(
            painter = painterResource(R.drawable.vinyl_center),
            contentDescription = "Vinyl",
            modifier = Modifier.size(120.dp)
        )
    }
}
