package com.example.soundbeat_test.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import java.io.File

private val urlsList: List<String> = listOf(
    "https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExMzBkcnRlcDZ6dmd0Mmo3NWUxYWxvZW14N2kyc3psenhhc2FrbHdzNyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/g1nJGgc9wgWw1prhuX/giphy.gif",
    "https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExd3Ywc2I0cWR1emdtZjJvY2RpMXo0bnJyZXBqYzZsemQwZTQxbnpyOCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/byP3o3dVJD03GiR0S2/giphy.gif",
    "https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExcHpyd29iMHVweWRlNGZ0NTMxYWZkeHhjcmM4OHUwYmZpYXRuODNveSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/jN9S0faVsUUfdaY50J/giphy.gif"
)

@Preview(showSystemUi = true)
@Composable
fun LeftColumnRightLargeGifLayout(
    urls: List<String> = urlsList, bigImageOnClick: () -> Unit = {
        println("Big image pressed")
    }, leftImageOnClick: () -> Unit = {
        println("Left image pressed")
    }, rightImageOnClick: () -> Unit = {
        println("Right image pressed")
    }
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            ImageGif(
                imageSource = urls[0], modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                bigImageOnClick.invoke()
            }
            ImageGif(
                imageSource = urls[1], modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                leftImageOnClick.invoke()
            }
        }
        ImageGif(
            imageSource = urls[2], modifier = Modifier
                .weight(1f)
                .padding(10.dp)
        ) {
            rightImageOnClick.invoke()
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TopLargeBottomRowGifLayout(
    urls: List<String> = urlsList, bigImageOnClick: () -> Unit = {
        println("Se ha presionado la imagen grande")
    }, leftImageOnClick: () -> Unit = {
        println("Se ha presionado la imagen de la izquierda")
    }, rightImageOnClick: () -> Unit = { println("Se ha presionado la imagen de la derecha") }
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create or inspect your playlists!")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f) // Ocupa la mitad del ancho disponible
            ) {
                ImageGif(
                    imageSource = urls[0],
                    modifier = Modifier
                        .fillMaxHeight() // Se ajusta a la altura del Row
                ) {
                    bigImageOnClick()
                }
                Text(
                    "Favorite songs!",
                    fontSize = 50.sp,
                    modifier = Modifier.offset(x = 68.dp, y = 30.dp),
                    color = Color.White
                )
            }
        }
        Text("Select a mode for create a playlist!")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Box(
                modifier = Modifier.weight(1f) // Ocupa la mitad del ancho disponible

            ) {
                ImageGif(
                    imageSource = urls[1],
                    modifier = Modifier
                        .fillMaxHeight() // Se ajusta a la altura del Row
                ) {
                    leftImageOnClick()
                }
                Text(
                    "Local mode",
                    fontSize = 40.sp,
                    modifier = Modifier.offset(x = 14.dp, y = 40.dp),
                    color = Color.White
                )
            }
            Box(
                modifier = Modifier.weight(1f) // Ocupa la mitad del ancho disponible
            ) {
                ImageGif(
                    imageSource = urls[2],
                    modifier = Modifier
                        .fillMaxHeight() // Se ajusta a la altura del Row
                ) {
                    rightImageOnClick()
                }
                Text(
                    "Remote mode",
                    fontSize = 40.sp,
                    modifier = Modifier.offset(x = 45.dp, y = 30.dp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ImageGif(
    imageSource: Any, modifier: Modifier = Modifier.size(150.dp), onClick: () -> Unit
) {
    val context = LocalContext.current

    val finalSource = when (imageSource) {
        is String -> {
            if (!imageSource.startsWith("http")) {
                val file = File(context.filesDir, imageSource)
                file.toUri()
            } else {
                imageSource
            }
        }

        else -> imageSource
    }

    Box(modifier = modifier.clickable { onClick() }) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(finalSource)
                .decoderFactory(GifDecoder.Factory()).build(),
            contentDescription = "GIF Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}