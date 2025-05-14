package com.example.soundbeat_test.ui.screens.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundbeat_test.network.registerUser
import com.example.soundbeat_test.network.userExists
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable del manejo del registro de usuarios.
 *
 * Esta clase gestiona la lógica relacionada con el registro de un nuevo usuario,
 * incluyendo la validación de campos, la llamada a la función de registro, y
 * el almacenamiento del correo en SharedPreferences si el registro fue exitoso.
 *
 * @property mensaje Un StateFlow que expone mensajes de estado o error relacionados con el proceso de registro.
 */
class RegisterViewModel : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)

    /**
     * Flow inmutable que expone mensajes al UI (por ejemplo, para mostrar un Toast).
     */
    val message: StateFlow<String?> = _message

    /**
     * Intenta registrar un nuevo usuario usando el correo y contraseña proporcionados.
     *
     * Valida que los campos no estén vacíos antes de proceder. Si el registro es exitoso,
     * se guarda el correo del usuario en las preferencias compartidas del dispositivo.
     *
     * @param email Correo electrónico del nuevo usuario.
     * @param password Contraseña del nuevo usuario.
     * @param context Contexto necesario para acceder a SharedPreferences.
     */
    fun registerUser(email: String, password: String, context: Context) {

        if (checkInputs(email, password)) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (checkUserExistence(email)) return@launch

                val resultantRegistry = registerUser(email, password)

                if (resultantRegistry == "Usuario registrado exitosamente!") {
                    saveUserData(context, email)
                }

                _message.value = resultantRegistry
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

        if (existe) {
            _message.value = "El usuario ya se encuentra registrado."
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

}