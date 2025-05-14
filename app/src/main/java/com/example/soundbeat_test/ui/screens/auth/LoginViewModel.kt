package com.example.soundbeat_test.ui.screens.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundbeat_test.network.loginUser
import com.example.soundbeat_test.network.userExists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar el proceso de inicio de sesión de usuarios.
 *
 * Expone el estado del mensaje y la autenticación para que la interfaz pueda reaccionar
 * a los cambios producidos durante la operación de login.
 */
class LoginViewModel : ViewModel() {
    /**
     * Mensaje emitido durante el proceso de inicio de sesión, como errores o éxito.
     */
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    /**
     * Indica si el usuario ha sido autenticado correctamente.
     */
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas.
     *
     * Primero valida los campos, luego comprueba si el usuario existe, y finalmente
     * realiza el inicio de sesión. Si todo va bien, guarda los datos del usuario en
     * preferencias compartidas.
     *
     * @param email Correo electrónico introducido por el usuario.
     * @param password Contraseña introducida por el usuario.
     * @param context Contexto de la aplicación, necesario para acceder a SharedPreferences.
     */
    fun logInUser(email: String, password: String, context: Context) {

        if (checkInputs(email, password)) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (checkUserExistence(email)) return@launch

                val resultadoLogin = loginUser(email, password)

                if (resultadoLogin == "Inicio de sesión exitoso!") {
                    saveUserData(context, email)
                }

                _message.value = resultadoLogin
                _isAuthenticated.value = true

            } catch (e: Exception) {
                _message.value = "Error de conexión: ${e.localizedMessage}"
            }
        }
    }

    /**
     * Verifica si un usuario con el email dado está registrado.
     *
     * Si no existe, se actualiza el mensaje de estado y se devuelve true para indicar
     * que no se puede continuar con el proceso de login.
     *
     * @param email Correo del usuario a verificar.
     * @return true si el usuario no está registrado (no existe), false si sí existe.
     */
    private suspend fun checkUserExistence(email: String): Boolean {
        val existe = userExists(email)

        if (!existe) {
            _message.value = "El usuario no está registrado."
            return true
        }
        return false
    }

    /**
     * Verifica si los campos de correo electrónico y contraseña están vacíos.
     *
     * Si alguno de los campos está vacío, emite un mensaje de error y retorna true para indicar
     * que los inputs no son válidos. Si ambos campos tienen contenido, retorna false.
     *
     * @param email El correo electrónico introducido por el usuario.
     * @param password La contraseña introducida por el usuario.
     * @return true si hay campos vacíos (inputs inválidos), false si ambos están correctamente rellenados.
     */
    private fun checkInputs(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            _message.value = "Email y contraseña no pueden estar vacíos."
            return true
        }
        return false
    }

    /**
     * Guarda los datos del usuario en las preferencias compartidas del dispositivo.
     *
     * Actualmente almacena solo el correo electrónico, utilizando un archivo de preferencias llamado "UserInfo".
     *
     * @param context Contexto necesario para acceder a SharedPreferences.
     * @param email Correo electrónico del usuario que se desea guardar.
     */
    private fun saveUserData(context: Context, email: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putString("email", email)
        editor.apply()
    }

}