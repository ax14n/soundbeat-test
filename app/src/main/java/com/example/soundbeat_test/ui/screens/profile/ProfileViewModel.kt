package com.example.soundbeat_test.ui.screens.profile


import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundbeat_test.network.getUserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de gestionar los datos del perfil del usuario.
 *
 * Utiliza `AndroidViewModel` para acceder de forma segura al contexto de la aplicación,
 * permitiendo recuperar el correo del usuario almacenado en `SharedPreferences` y
 * obtener su información desde una fuente remota.
 *
 * @constructor Recibe la instancia de [Application] necesaria para acceder a recursos del sistema.
 */
class ProfileViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _userInfo = MutableStateFlow<Map<String, Any>?>(null)

    /**
     * Flujo de datos que contiene la información del usuario si se ha recuperado correctamente.
     */
    val userInfo: StateFlow<Map<String, Any>?> = _userInfo

    private val _error = MutableStateFlow<String?>(null)

    /**
     * Flujo de datos que contiene mensajes de error si ocurre algún problema durante la obtención del perfil.
     */
    val error: StateFlow<String?> = _error

    init {
        val email = getSavedEmail()
        if (email != null) {
            getProfile(email)
        } else {
            _error.value = "No se encontró el email en preferencias."
        }
    }

    /**
     * Recupera el correo electrónico almacenado en `SharedPreferences`.
     *
     * @return El correo electrónico del usuario o `null` si no existe.
     */
    private fun getSavedEmail(): String? {
        val prefs = getApplication<Application>()
            .getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        return prefs.getString("email", null)
    }

    /**
     * Obtiene la información del perfil del usuario de forma asíncrona y la guarda en el estado.
     *
     * @param email Correo electrónico del usuario cuyo perfil se desea obtener.
     */
    fun getProfile(email: String) {
        viewModelScope.launch {
            try {
                val result = getUserInfo(email)

                if (result.isSuccess) {
                    val data = result.getOrNull()
                    _userInfo.value = data
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido"
                    _error.value = errorMsg
                }

            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            }
        }
    }

}