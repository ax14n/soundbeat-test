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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import java.io.File

private val urlsList: List<String> = listOf(
    "https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExdm9xZXBtZWtmY2VqczB1eXFlOWdsMWVtcGd6NWRhdm5laDkydDVrNSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/0PhpEWpqHZzm5tTcLM/giphy.gif",
    "https://media2.giphy.com/media/v1.Y2lkPTc5MGI3NjExNms3bHUyejNkeXMxOG8xcjFrbDhtZDFzdG5oemx6cjQ2ODdhZ2hueCZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/jObP9cWJTF0Pb7UjuA/giphy.gif",
    "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExaHAzbmFneWk2d2ZxZ3Y2bjUyczhkbGNjcXppdWQxdDBiN2k0ZXB3MiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/3ohryiYkE0DVwdLAys/giphy.gif"
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
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp)
        ) {
            ImageGif(
                imageSource = urls[0], modifier = Modifier.fillMaxWidth()
            ) {
                bigImageOnClick()
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ImageGif(
                imageSource = urls[1],
                modifier = Modifier
                    .weight(1f) // Ocupa la mitad del ancho disponible
                    .fillMaxHeight() // Se ajusta a la altura del Row
            ) {
                leftImageOnClick()
            }

            ImageGif(
                imageSource = urls[2],
                modifier = Modifier
                    .weight(1f) // También ocupa la mitad del ancho disponible
                    .fillMaxHeight() // Se ajusta a la altura del Row
            ) {
                rightImageOnClick()
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