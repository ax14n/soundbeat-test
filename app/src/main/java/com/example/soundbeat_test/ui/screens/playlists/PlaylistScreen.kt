package com.example.soundbeat_test.ui.screens.playlists

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.soundbeat_test.data.Playlist
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.ui.components.AlbumHorizontalList
import com.example.soundbeat_test.ui.components.TopLargeBottomRowGifLayout
import com.example.soundbeat_test.ui.selected_playlist.SharedPlaylistViewModel

@Composable
fun PlaylistScreen(
    navHostController: NavHostController? = null,
    playlistScreenViewModel: PlaylistScreenViewModel,
    sharedPlaylistViewModel: SharedPlaylistViewModel
) {
    val playlists = playlistScreenViewModel.userPlaylists.collectAsState().value

    Box {
        Scaffold { padding ->

            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                TopLargeBottomRowGifLayout(
                    bigImageOnClick = {  /* TODO("Not yet implemented") */ },
                    leftImageOnClick = { /* TODO("Not yet implemented") */ },
                    rightImageOnClick = { /* TODO("Not yet implemented") */ })
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text("¡Tus playlists en línea!")
                    AlbumHorizontalList(
                        list = playlists,
                        sharedPlaylistViewModel = sharedPlaylistViewModel
                    ) { item ->
                        if (item is Playlist) {
                            Log.d("PlaylistScreen", "Playlist: ${item.id} - ${item.name}")
                            sharedPlaylistViewModel.updatePlaylist(item)
                            Log.d(
                                "PlaylistScreen",
                                "${sharedPlaylistViewModel.selectedPlaylist.value}"
                            )

                            navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
                            Log.d("PlaylistScreen", "Navigating to: SELECTED PLAYLIST")
                        }

                        navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
                        Log.d("PlaylistScreen", "Navigating to: SELECTED PLAYLIST")
                    }
                    Text("¡Tus playlist locales!")
                    AlbumHorizontalList(sharedPlaylistViewModel = sharedPlaylistViewModel) {
                        navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
                        Log.d("PlaylistScreen", "Navigating to: SELECTED PLAYLIST")
                    }
                }
            }
        }

    }
}