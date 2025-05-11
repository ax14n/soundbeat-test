package com.example.soundbeat_test.ui.selected_playlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.soundbeat_test.R
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.ui.audio.AudioPlayerViewModel
import com.example.soundbeat_test.ui.components.UserImage
import com.example.soundbeat_test.ui.screens.search.VinylList

@Preview(showSystemUi = true)
@Composable
fun SelectedPlaylistScreen(
    navHostController: NavHostController? = null,
    sharedPlaylistViewModel: SharedPlaylistViewModel? = null,
    audioPlayerViewModel: AudioPlayerViewModel? = null
) {
    val playlistState = sharedPlaylistViewModel?.selectedPlaylist?.collectAsState()

    val playlist = playlistState?.value

    LaunchedEffect(playlist) {
        Log.d(
            "SelectedPlaylistScreen",
            "Playlist recibida: ${playlist?.name} con ${playlist?.songs!!.size} álbum(es)"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3E3E3))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.arrow_back),
            contentDescription = "Go back",
            modifier = Modifier
                .clickable(onClick = {
                    navHostController?.navigate(ROUTES.HOME) {
                        popUpTo(ROUTES.HOME) { inclusive = true }
                    }
                })
                .align(Alignment.End)
                .padding(top = 10.dp)
        )
        Spacer(Modifier.padding(top = 20.dp))

        UserImage()

        playlist.let {

            Text(
                text = playlist?.name ?: "Unsigned",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "id = " + playlist?.id.toString(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp),
                fontFamily = FontFamily.Monospace
            )

            Spacer(Modifier.padding(top = 10.dp))

            Button(
                onClick = {
                    // TODO("Start playing playlist songs")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "REPRODUCE VINYLS", fontWeight = FontWeight.Bold
                )
            }

            VinylList(
                albumList = playlist?.songs!!.toList()
            ) { album ->
                val url: String = audioPlayerViewModel?.createSongUrl(album).toString()
                audioPlayerViewModel?.loadAndPlayHLS(url)
                Log.d("SelectedPlaylistScreen", "Started playing ${album.name} by ${album.author}")
            }

        }

    }
}