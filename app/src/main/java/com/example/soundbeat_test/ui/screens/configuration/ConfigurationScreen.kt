package com.example.soundbeat_test.ui.screens.configuration

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding), verticalArrangement = Arrangement.spacedBy(10.dp)

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
                    Text("¡Configura todo lo que quieras!")
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
                Text(text = "Configuraciones")
                SettingsButton() {
                    println("hola")
                }
                SettingsButton() {}
                SettingsButton() {}
                SettingsButton() {}
                SettingsButton() {}
                SettingsButton() {}

            }
        }
    }
}

@Composable
fun SettingsButton(
    text: String = "Not implemented", onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Gray, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(2.dp) // Agregamos padding interno
    ) {
        Text(text = text, modifier = Modifier.padding(10.dp), color = Color.White)
    }
}
