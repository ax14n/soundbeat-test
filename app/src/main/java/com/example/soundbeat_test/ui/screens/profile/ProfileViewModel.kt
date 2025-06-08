package com.example.soundbeat_test.ui.screens.profile

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.soundbeat_test.network.getUserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

    private val _userInfo = MutableStateFlow<Map<String, Any>>(mapOf())
    val userInfo: StateFlow<Map<String, Any>> = _userInfo

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        prefs.getString("email", null)?.let {
            fetchUserProfile(it)
        } ?: run {
            _error.value = "No se encontró el email en preferencias."
        }
    }

    fun fetchUserProfile(email: String) {
        viewModelScope.launch {
            when (email) {
                "OFFLINE" -> {
                    _userInfo.value = mapOf("username" to "OFFLINE USER")
                }

                else -> {
                    val result = runCatching { getUserInfo(email) }
                    result.onSuccess { _userInfo.value = it.getOrDefault(mapOf()) }
                        .onFailure { _error.value = it.message ?: "Unknown error" }
                }
            }
        }
    }
}
