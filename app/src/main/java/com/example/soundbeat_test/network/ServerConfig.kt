package com.example.soundbeat_test.network

import android.content.Context

object ServerConfig {
    private var baseUrl: String? = null

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val ip = prefs.getString("address", "192.168.1.152") ?: "192.168.1.152"
        val port = "8080"
        val protocol = "http"
        baseUrl = "$protocol://$ip:$port"
    }

    fun getBaseUrl(): String {
        return baseUrl ?: throw IllegalStateException("ServerConfig not initialized")
    }

    fun updateIp(context: Context, newIp: String) {
        val prefs = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        prefs.edit().putString("address", newIp).apply()
        init(context)
    }
}
