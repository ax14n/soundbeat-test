package com.example.soundbeat_test.ui.screens.auth

import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.soundbeat_test.R
import com.example.soundbeat_test.navigation.ROUTES

/**
 * Pantalla de inicio de sesión que permite al usuario introducir su correo y contraseña,
 * con opciones para navegar al registro o acceder a la pantalla principal de la app.
 *
 * @param navHostController Controlador de navegación utilizado para cambiar de pantalla.
 */
@Preview(showSystemUi = true)
@Composable
fun LoginScreen(
    navHostController: NavHostController? = null, loginViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val isAuthenticated by loginViewModel.isAuthenticated.collectAsState()
    val message by loginViewModel.message.collectAsState()

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.auth_background),
            contentDescription = "Auth background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Card(
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Text(
                        text = "Sound-beat",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.padding(bottom = 16.dp),
                        style = TextStyle(
                            shadow = Shadow(Color.Black, Offset(2f, 2f), 4f)
                        )
                    )
                }

                Text(
                    text = "Login",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Enter your email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Enter your password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Register here!",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { navHostController?.let { onLoginClick(navHostController) } })

                Spacer(modifier = Modifier.height(16.dp))

                message?.let {
                    Text(
                        text = it,
                        color = if (it.startsWith("Error")) Color.Red else Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        navHostController?.let {
                            loginViewModel.logInUser(
                                email = email.value.trim(),
                                password = password.value.trim(),
                                loginModes = LoginModes.ONLINE_MODE
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDE7028)), // lavanda
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "ENTER!", fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        navHostController?.let {
                            loginViewModel.logInUser(
                                email = email.value.trim(),
                                password = password.value.trim(),
                                loginModes = LoginModes.OFFLINE_MODE
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF013603)), // lavanda
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "Offline Mode", fontWeight = FontWeight.Bold
                    )
                }

                LaunchedEffect(isAuthenticated) {
                    if (isAuthenticated) {
                        onEnterClick(navHostController!!)
                    }
                }

            }
        }
    }
}

/**
 * Maneja el evento de clic en el texto "Register here!" y navega a la pantalla de registro.
 *
 * @param navHostController Controlador de navegación usado para redirigir al registro.
 */
private fun ColumnScope.onLoginClick(navHostController: NavHostController) {
    navHostController.navigate(ROUTES.REGISTER) {
        popUpTo(ROUTES.LOGIN) { inclusive = true }
    }
    Log.d("LoginScreen", "Navigating to: REGISTER SCREEN")
}

/**
 * Maneja el evento de clic en el botón "ENTER!" y navega a la pantalla principal (HOME).
 *
 * @param navHostController Controlador de navegación usado para redirigir al home.
 */
private fun ColumnScope.onEnterClick(navHostController: NavHostController) {
    navHostController.navigate(ROUTES.HOME) {
        popUpTo(ROUTES.LOGIN) { inclusive = true }
    }
    Log.d("LoginScreen", "Navigating to: HOME SCREEN")
}
