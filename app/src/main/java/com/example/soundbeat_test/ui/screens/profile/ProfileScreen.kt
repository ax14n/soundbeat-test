package com.example.soundbeat_test.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.ui.components.AlbumHorizontalList
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
fun ProfileScreen(navHostController: NavHostController? = null) {

    val username: String by remember { mutableStateOf("AX14N") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3E3E3))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .clickable {
                    navHostController?.let { onClickConfigurationButton(navHostController) }
                },
            contentAlignment = Alignment.TopEnd
        ) {

            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
            )

        }
        UserImage()

        Text(
            text = username,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp),
            fontFamily = FontFamily.Monospace
        )

        SoundbeatContentRow() {
            AlbumHorizontalList()
        }
        SoundbeatContentRow() {
            AlbumHorizontalList()
        }
        SoundbeatContentRow() {
            AlbumHorizontalList()
        }
    }
}

/**
 * Composable que representa una fila contenedora de contenido relacionado con Soundbeat,
 * como listas de álbumes o playlists.
 *
 * @param content Contenido que se colocará dentro de la fila.
 */
@Composable
private fun SoundbeatContentRow(content: @Composable () -> Unit) {
    Spacer(modifier = Modifier.height(24.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
            .background(Color(0xFFDCDCDC)),
        contentAlignment = Alignment.Center
    ) {
        content()
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