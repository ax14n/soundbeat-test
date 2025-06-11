package com.example.soundbeat_test.ui.screens.configuration

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundbeat_test.network.ServerConfig
import com.example.soundbeat_test.network.setEmail
import com.example.soundbeat_test.network.setPassword
import com.example.soundbeat_test.network.setUsername
import kotlinx.coroutines.launch

class ConfigurationViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * Cambia el username del actual usuario de la aplicación por uno nuevo.
     */
    fun changeUsername(newUsername: String) {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("UserInfo", MODE_PRIVATE)
            val email = prefs.getString("email", "OFFLINE")
            setUsername(
                email = email.toString(), newUsername = newUsername
            )
        }
    }

    /**
     * Cambia el email del usuario actual.
     */
    fun changeEmail(newEmail: String) {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("UserInfo", MODE_PRIVATE)
            val email = prefs.getString("email", "OFFLINE")
            setEmail(
                email = email.toString(), newEmail = newEmail
            )

        }
    }

    /**
     * Cambia la clave del usuario actual.
     */
    fun changePassword(newPassword: String) {
        viewModelScope.launch {
            val prefs = getApplication<Application>().getSharedPreferences("UserInfo", MODE_PRIVATE)
            val email = prefs.getString("email", "OFFLINE")
            setPassword(
                email = email.toString(), newPassword = newPassword
            )

        }
    }

    fun changeIPAddress(address: String) {
        val context = getApplication<Application>()
        val prefs = context.getSharedPreferences("UserInfo", MODE_PRIVATE)

        prefs.edit().putString("address", address).apply()

        val address = prefs.getString("address", "192.168.1.152") ?: "192.168.1.152"

        ServerConfig.updateIp(context, address)
    }

    fun changeMusicDirectory(launcher: ManagedActivityResultLauncher<Uri?, Uri?>) {
        launcher.launch(null)
    }

}