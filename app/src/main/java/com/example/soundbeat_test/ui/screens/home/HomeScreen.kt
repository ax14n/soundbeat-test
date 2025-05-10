package com.example.soundbeat_test.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.network.getServerSongs
import com.example.soundbeat_test.ui.components.AlbumHorizontalList
import com.example.soundbeat_test.ui.components.LeftColumnRightLargeGifLayout

@Preview(showSystemUi = true)
@Composable
fun HomeScreen(navHostController: NavHostController? = null) {
    Scaffold { padding ->

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(15.dp)

        ) {
            LeftColumnRightLargeGifLayout()
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                ListServerSongs(
                    "¡Canciones del servidor!",
                    navHostController = navHostController,
                    genre = null
                )
                ListServerSongs(
                    "¡Tus canciones favoritas remotas!",
                    navHostController = navHostController,
                    genre = null
                )
                ListServerSongs(
                    "¡Canciones locales!",
                    navHostController = navHostController,
                    genre = null
                )
                ListServerSongs(
                    "¡Tus canciones favoritas locales!",
                    navHostController = navHostController,
                    genre = null
                )
            }
        }
    }
}

@Composable
fun ListServerSongs(text: String, genre: String?, navHostController: NavHostController?) {
    var songsList by remember { mutableStateOf<List<Album>>(emptyList<Album>()) }
    LaunchedEffect(Unit) {
        val result = getServerSongs(genre ?: "null")
        if (result.isSuccess) {
            songsList = result.getOrNull() ?: emptyList()
        }
    }
    Text(text)
    AlbumHorizontalList(songsList) {
        navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
        Log.d("PlaylistScreen", "Navigating to: SELECTED PLAYLIST")
    }
}