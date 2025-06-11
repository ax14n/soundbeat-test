package com.example.soundbeat_test.ui.screens.playlists

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val prefs = remember { context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE) }
    val email = remember {
        derivedStateOf {
            prefs.getString("email", "OFFLINE")
        }
    }.value
    Log.d("PlaylistScreen", "email: $email")

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val prefs = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
                val updatedEmail = prefs.getString("email", "OFFLINE")

                if (updatedEmail != "OFFLINE") {
                    playlistScreenViewModel.obtainRemoteUserPlaylists()
                }
                playlistScreenViewModel.obtainLocalUserPlaylists()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val remotePlaylists = playlistScreenViewModel.remoteUserPlaylists.collectAsState().value
    val remotePlaylistError = playlistScreenViewModel.remotePlaylistError.collectAsState().value

    val localPlaylists = playlistScreenViewModel.localUserPlaylist.collectAsState().value


    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.padding(7.dp), verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Card(
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Column(
                    modifier = Modifier.padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopLargeBottomRowGifLayout(
                        bigImageOnClick = {  /* TODO("Not yet implemented") */ },
                        leftImageOnClick = {
                            navHostController?.navigate("PLAYLIST_CREATOR/${CreationMode.OFFLINE_PLAYLIST.name}")
                        },
                        rightImageOnClick = {
                            navHostController?.navigate("PLAYLIST_CREATOR/${CreationMode.ONLINE_PLAYLIST.name}")
                        })
                }
            }

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp)
            ) {
                Text("Your remote playlists!")
                showRemoteUserPlaylists(
                    email,
                    remotePlaylistError,
                    remotePlaylists,
                    sharedPlaylistViewModel,
                    navHostController
                )
            }

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("Your local playlists!")
                showLocalUserPlaylists(
                    localPlaylists, sharedPlaylistViewModel, navHostController
                )
            }

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp)
            ) {
                Text("Based in what you heard!")
                ComingSoonMessage()
            }

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp)
            ) {
                Text("Here are some auto-generated rock playlist created using your local songs!")
                ComingSoonMessage()
            }
        }
    }


}

@Composable
private fun showLocalUserPlaylists(
    localPlaylists: List<Playlist>,
    sharedPlaylistViewModel: SharedPlaylistViewModel,
    navHostController: NavHostController?
) {
    if (localPlaylists.isEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            NoPlaylistsFoundMessage(null)
        }
    } else {
        AlbumHorizontalList(list = localPlaylists) { item ->
            if (item is Playlist) {
                sharedPlaylistViewModel.setMode(selectionMode = SelectionMode.PLAYLIST)
                sharedPlaylistViewModel.setSongsSource(songsSource = SongSource.LOCALS)
                Log.d("PlaylistScreen", "Playlist: ${item.id} - ${item.name}")
                sharedPlaylistViewModel.updatePlaylist(item)
                Log.d(
                    "PlaylistScreen", "${sharedPlaylistViewModel.selectedPlaylist.value}"
                )

                navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
                Log.d("PlaylistScreen", "Navigating to: SELECTED PLAYLIST")
            }

            navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
            Log.d("PlaylistScreen", "Navigating to: SELECTED PLAYLIST")
        }
    }
}

@Composable
private fun showRemoteUserPlaylists(
    email: String?,
    remotePlaylistError: String?,
    remotePlaylists: List<Playlist>,
    sharedPlaylistViewModel: SharedPlaylistViewModel,
    navHostController: NavHostController?
) {
    val remoteMessage = when {
        email == "OFFLINE" -> "You're in OFFLINE MODE"
        remotePlaylistError != null -> remotePlaylistError
        remotePlaylists.isEmpty() -> "No playlist"
        else -> null
    }

    if (remoteMessage != null) {
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            NoPlaylistsFoundMessage(remoteMessage)
        }
    } else {
        AlbumHorizontalList(
            list = remotePlaylists
        ) { item ->
            if (item is Playlist) {
                sharedPlaylistViewModel.setMode(selectionMode = SelectionMode.PLAYLIST)
                sharedPlaylistViewModel.setSongsSource(songsSource = SongSource.REMOTES)
                sharedPlaylistViewModel.updatePlaylist(item)
                Log.d("PlaylistScreen", "Playlist: ${item.id} - ${item.name}")
                Log.d(
                    "PlaylistScreen", "${sharedPlaylistViewModel.selectedPlaylist.value}"
                )
                navHostController?.navigate(ROUTES.SELECTED_PLAYLIST)
                Log.d("PlaylistScreen", "Navigating to: SELECTED PLAYLIST")
            }
        }
    }
}

@Composable
private fun NoPlaylistsFoundMessage(customizedMessage: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = customizedMessage ?: "No playlists were found",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun ComingSoonMessage(customizedMessage: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = customizedMessage ?: "Coming Soon",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}
