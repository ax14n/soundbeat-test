package com.example.soundbeat_test.ui.screens.playlists

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                playlistScreenViewModel.obtainRemoteUserPlaylists()
                playlistScreenViewModel.obtainLocalUserPlaylists()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val remotePlaylists = playlistScreenViewModel.remoteUserPlaylists.collectAsState().value
    val remoteError = playlistScreenViewModel.error.collectAsState().value

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
                    if (remotePlaylists.isNotEmpty()) {
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
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Red),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Sin conexión",
                                color = (Color.White),
                                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                            )
                        }
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