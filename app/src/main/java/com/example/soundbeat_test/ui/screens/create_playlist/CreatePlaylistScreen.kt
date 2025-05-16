package com.example.soundbeat_test.ui.screens.create_playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.ui.components.UserImage
import kotlinx.coroutines.flow.MutableStateFlow

@Preview(showSystemUi = true)
@Composable
fun CreatePlaylist() {

    val playlistName: String = MutableStateFlow<String>("Playlist nº1").collectAsState().value
    val songsSet: Set<Album> =
        MutableStateFlow<Set<Album>>(Album.AlbumListExample.toSet()).collectAsState().value
    val isVisible = remember { mutableStateOf<Boolean>(false) }


    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        UserImage()
        // OutlinedTextField(value = playlistName, onValueChange = )
    }

}