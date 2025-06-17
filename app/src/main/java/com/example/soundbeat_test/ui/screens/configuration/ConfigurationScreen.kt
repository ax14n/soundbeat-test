package com.example.soundbeat_test.ui.screens.configuration

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.soundbeat_test.R
import com.example.soundbeat_test.local.LocalConfig
import com.example.soundbeat_test.navigation.ROUTES
import com.example.soundbeat_test.ui.components.ImageGif
import com.example.soundbeat_test.utils.SimpleAlertDialog
import com.example.soundbeat_test.utils.TextInputDialog

@Preview(showSystemUi = true)
@Composable
fun ConfigurationScreen(
    navHostController: NavHostController? = null,
    configurationViewModel: ConfigurationViewModel = viewModel()
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE) }

    val activeDialog = remember { mutableStateOf<String?>(null) }
    val temporalBuffer = remember { mutableStateOf("") }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri?.let {
                val path = getFullPathFromTreeUri(uri, context)
                Log.d("%%%", uri.toString())
                Log.d("%%%", path ?: "Ruta nula")

                if (path != null) {
                    LocalConfig.setMusicDirectory(context, path)
                }
            }
        }

    dialogHandler(
        context = context,
        prefs = prefs,
        activeDialog = activeDialog,
        temporalBuffer = temporalBuffer,
        configurationViewModel = configurationViewModel,
        navHostController = navHostController
    )

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
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
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
                text = "Configurations", modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text("Remote settings")
            SettingsButton("Change username") {
                activeDialog.value = "username"
            }

            SettingsButton("Change email") {
                activeDialog.value = "email"
            }

            SettingsButton("Change password") {
                activeDialog.value = "password"
            }

            Spacer(modifier = Modifier.padding(vertical = 2.dp))
            Text("Local settings")
            SettingsButton("Change music directory") {
                configurationViewModel.changeMusicDirectory(launcher)
            }
            SettingsButton("Change the app theme") {}
            SettingsButton("Change server address") {
                activeDialog.value = "address"
            }
            SettingsButton(text = "Tutorial") {
                val mode = prefs.getString("tutorial", "OFF")
                if (mode == "OFF") {
                    prefs.edit().putString("tutorial", "ON").apply()
                    Toast.makeText(context, "Tutorial: ON", Toast.LENGTH_SHORT).show()

                } else {
                    prefs.edit().putString("tutorial", "OFF").apply()
                    Toast.makeText(context, "Tutorial: OFF", Toast.LENGTH_SHORT).show()
                }
            }

            Spacer(Modifier.padding(top = 10.dp))
            SettingsButton(text = "Log out", color = Color.Red) {
                activeDialog.value = "logout"
            }
            Text(
                "Created by Zelmar Hernán Ramilo Piazzoli", modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun dialogHandler(
    context: Context,
    prefs: SharedPreferences,
    activeDialog: MutableState<String?>,
    temporalBuffer: MutableState<String>,
    configurationViewModel: ConfigurationViewModel,
    navHostController: NavHostController?
) {
    when (activeDialog.value) {
        "username" -> {
            val email = prefs.getString("email", "OFFLINE")

            if (email == "OFFLINE") {
                Toast.makeText(
                    context, "You're in OFFLINE MODE", Toast.LENGTH_SHORT
                ).show()
            } else {

                TextInputDialog(
                    title = "Change your account username!",
                    initialText = temporalBuffer.value,
                    onDismissRequest = { activeDialog.value = null },
                    onConfirm = { input ->
                        temporalBuffer.value = input
                        configurationViewModel.changeUsername(input)
                        activeDialog.value = null
                    })
            }
        }

        "email" -> {

            val email = prefs.getString("email", "OFFLINE")

            if (email == "OFFLINE") {
                Toast.makeText(
                    context, "You're in OFFLINE MODE", Toast.LENGTH_SHORT
                ).show()
            } else {
                TextInputDialog(
                    title = "Change your account email!",
                    initialText = temporalBuffer.value,
                    onDismissRequest = { activeDialog.value = null },
                    onConfirm = { input ->
                        temporalBuffer.value = input
                        configurationViewModel.changeEmail(input)
                        activeDialog.value = null
                    })
            }
        }

        "password" -> {
            val email = prefs.getString("email", "OFFLINE")

            if (email == "OFFLINE") {
                Toast.makeText(
                    context, "You're in OFFLINE MODE", Toast.LENGTH_SHORT
                ).show()
            } else {
                TextInputDialog(
                    title = "Change your account password!",
                    initialText = "",
                    onDismissRequest = { activeDialog.value = null },
                    onConfirm = { input ->
                        configurationViewModel.changePassword(input)
                        activeDialog.value = null
                    })
            }
        }

        "logout" -> SimpleAlertDialog(
            dialogTitle = "Log out of your account?",
            dialogSubTitle = "You will need to log in again to access your music library.",
            onDismissRequest = { activeDialog.value = null },
            onConfirmation = {
                activeDialog.value = null
                onCloseSessionClick(navHostController!!)
            })

        "address" -> TextInputDialog(
            title = "Change the IP Address",
            initialText = "",
            onDismissRequest = { activeDialog.value = null },
            onConfirm = { input ->
                configurationViewModel.changeIPAddress(input)
                activeDialog.value = null
            })

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
private fun onCloseSessionClick(navHostController: NavHostController) {
    navHostController.navigate(ROUTES.LOGIN) {
        popUpTo(ROUTES.SETTINGS) { inclusive = true }
    }
    Log.d("LoginScreen", "Navigating to: HOME SCREEN")
}

fun getFullPathFromTreeUri(uri: Uri, context: Context): String? {
    val docId = DocumentsContract.getTreeDocumentId(uri) // p.ej. "primary:Music/Telegram"
    val parts = docId.split(":")
    if (parts.isEmpty()) return null

    val type = parts[0] // "primary"
    val relativePath = if (parts.size > 1) parts[1] else ""

    val basePath = when (type) {
        "primary" -> Environment.getExternalStorageDirectory().absolutePath // "/storage/emulated/0"
        else -> "/storage/$type"
    }

    return "$basePath/${relativePath}".trimEnd('/')
}

