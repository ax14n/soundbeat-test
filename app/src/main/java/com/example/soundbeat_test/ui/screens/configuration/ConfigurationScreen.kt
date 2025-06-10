package com.example.soundbeat_test.ui.screens.configuration

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.soundbeat_test.R
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.ui.components.ImageGif

@Preview(showSystemUi = true)
@Composable
fun ConfigurationScreen(navHostController: NavHostController? = null) {
    val url =
        "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExaGN4MG9nMHdiZXd0aDJ6OGF6ejU0Y3J4Z2ZpNnpuM3hrcjJ5ZnhvZiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/zYoVn6EN9mM8VQFbOd/giphy.gif"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Go back",
                    modifier = Modifier.clickable(onClick = {
                        navHostController?.navigate(ROUTES.HOME) {
                            popUpTo(ROUTES.HOME) { inclusive = true }
                        }
                    })
                )
                Text("Set up everything you want!")
            }
            ImageGif(
                imageSource = url, modifier = Modifier.size(width = 500.dp, height = 150.dp)
            ) {}
        }
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Configurations",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text("Remote settings")
            SettingsButton("Change username") {}
            SettingsButton("Change email") {}
            SettingsButton("Change password") {}
            Spacer(modifier = Modifier.padding(vertical = 2.dp))
            Text("Local settings")
            SettingsButton("Change music directory") {}
            SettingsButton("Change the app theme") {}
            SettingsButton("Change server address") {}

            Spacer(Modifier.padding(top = 10.dp))
            SettingsButton(text = "Log out", color = Color.Red) {
                onCloseSessionClick(navHostController!!)
            }
            Text(
                "Created by Zelmar Hernán Ramilo Piazzoli",
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


@Composable
fun SettingsButton(
    text: String = "Not implemented", color: Color = Color.Gray, onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = color, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(2.dp)) {
        Text(text = text, modifier = Modifier.padding(10.dp), color = Color.White)
    }
}

/**
 * Maneja el evento de clic en el botón "ENTER!" y navega a la pantalla principal (HOME).
 *
 * @param navHostController Controlador de navegación usado para redirigir al home.
 */
private fun ColumnScope.onCloseSessionClick(navHostController: NavHostController) {
    navHostController.navigate(ROUTES.LOGIN) {
        popUpTo(ROUTES.SETTINGS) { inclusive = true }
    }
    Log.d("LoginScreen", "Navigating to: HOME SCREEN")
}
