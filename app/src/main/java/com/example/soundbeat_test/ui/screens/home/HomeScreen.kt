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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.navigation.ROUTES
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
                Text("¡En base a lo que has escuchado!")
                AlbumHorizontalList(listOf(Album.AlbumExample, Album.AlbumExample)) {
                    navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
                    Log.d("PlaylistScreen", "Navigating to: SELECTED PLAYLIST")
                }
                Text("¡Canciones que te han gustado!")
                AlbumHorizontalList(listOf(Album.AlbumExample, Album.AlbumExample)) {
                    navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
                    Log.d("PlaylistScreen", "Navigating to: SELECTED PLAYLIST")
                }
            }
        }
    }
}