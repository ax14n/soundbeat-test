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
import com.example.soundbeat_test.ui.screens.create_playlist.CreationMode
import com.example.soundbeat_test.ui.screens.selected_playlist.SelectionMode
import com.example.soundbeat_test.ui.screens.selected_playlist.SharedPlaylistViewModel
import com.example.soundbeat_test.ui.screens.selected_playlist.SongSource

@Composable
fun PlaylistScreen(
    navHostController: NavHostController? = null,
    playlistScreenViewModel: PlaylistScreenViewModel,
    sharedPlaylistViewModel: SharedPlaylistViewModel
) {
    val remotePlaylists = playlistScreenViewModel.remoteUserPlaylists.collectAsState().value
    val localPlaylists = playlistScreenViewModel.localUserPlaylist.collectAsState().value

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
                    leftImageOnClick = {
                        navHostController?.navigate("PLAYLIST_CREATOR/${CreationMode.OFFLINE_PLAYLIST.name}")
                    },
                    rightImageOnClick = {
                        navHostController?.navigate("PLAYLIST_CREATOR/${CreationMode.ONLINE_PLAYLIST.name}")
                    })
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Text("¡Tus playlists en línea!")
                    AlbumHorizontalList(
                        list = remotePlaylists
                    ) { item ->
                        if (item is Playlist) {
                            sharedPlaylistViewModel.setMode(selectionMode = SelectionMode.PLAYLIST)
                            sharedPlaylistViewModel.setSongsSource(songsSource = SongSource.REMOTES)
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
                    AlbumHorizontalList(list = localPlaylists) { item ->
                        if (item is Playlist) {
                            sharedPlaylistViewModel.setMode(selectionMode = SelectionMode.PLAYLIST)
                            sharedPlaylistViewModel.setSongsSource(songsSource = SongSource.LOCALS)
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
                }
            }
        }

    }
}