package com.example.soundbeat_test.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.ui.components.FavoritePlaylist
import com.example.soundbeat_test.ui.components.UserImage

/**
 * Composable que representa la pantalla de perfil de usuario.
 *
 * Muestra la imagen de perfil, el nombre de usuario, un botón de configuración
 * y varias filas con listas de contenido del tipo álbum o playlist.
 *
 * @param navHostController Controlador de navegación usado para redirigir a la pantalla de configuración.
 */
@Preview(showSystemUi = true)
@Composable
fun ProfileScreen(
    navHostController: NavHostController? = null, profileViewModel: ProfileViewModel = viewModel()
) {

    val userInfo = profileViewModel.userInfo.collectAsState().value
    val error = profileViewModel.error.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3E3E3))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingsIcon(navHostController)
        UserImage()
        Spacer(Modifier.padding(10.dp))
        when {
            error != null -> {
                Text("Error al cargar el perfil", color = Color.Red)
            }

            userInfo != null -> {
                val name = userInfo?.get("username")?.toString() ?: "[Sin nombre]"
                Text(name)
            }
        }
        Spacer(modifier = Modifier.padding(vertical = 10.dp))

        OutlinedCard(
            modifier = Modifier
                .background(Color(0xFFE8E8E8))
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FavoriteSongsSection()
                StatsSection()
            }
        }

    }
}

@Composable
private fun StatsSection() {
    SectionTitle("Stats zone!")
    Row(
    ) {
        Column(
        ) {
            Text("Playlists count:")
            Text("0")
        }
        Spacer(modifier = Modifier.padding(horizontal = 10.dp))
        Column(
        ) {
            Text("Most reproduced song:")
            Text("Empty")
        }

    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title)
    Spacer(modifier = Modifier.padding(vertical = 5.dp))
}

@Composable
private fun FavoriteSongsSection() {
    SectionTitle("Your special playlist")
    FavoritePlaylist() {
        Log.d("ProfileScreen", "user wanna see his favorite songs")
    }
    Spacer(modifier = Modifier.padding(vertical = 10.dp))
}


@Composable
private fun SettingsIcon(navHostController: NavHostController?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp)
            .clickable {
                navHostController?.let { onClickConfigurationButton(navHostController) }
            }, contentAlignment = Alignment.TopEnd
    ) {

        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings",
            modifier = Modifier
                .padding(8.dp)
                .size(24.dp),
        )
    }
}

/**
 * Función que se ejecuta al hacer clic en el icono de configuración.
 *
 * Redirige al usuario a la pantalla de configuración.
 *
 * @param navHostController Controlador de navegación que permite realizar la transición.
 */
private fun onClickConfigurationButton(navHostController: NavHostController) {
    navHostController.navigate(ROUTES.SETTINGS)
    Log.d("ProfileScreen", "Navigating to: CONFIGURATION SCREEN")
}