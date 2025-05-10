package com.example.soundbeat_test.network

import android.util.Log
import com.example.soundbeat_test.data.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

const val URL_BASE = "http://192.168.1.174:8080"

/**
 * Función para hacer peticiones a la API, tanto GET como POST, con el manejo adecuado de respuestas y errores.
 */
private suspend fun makeApiRequest(
    url: String, method: String = "GET", jsonBody: JSONObject? = null
): String = withContext(Dispatchers.IO) {
    try {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = method
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

        // Si es un POST, agregar el cuerpo JSON
        jsonBody?.let {
            connection.doOutput = true
            OutputStreamWriter(connection.outputStream).use { it.write(it.toString()) }
        }

        val responseCode = connection.responseCode
        val response = if (responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream.bufferedReader().readText()
        } else {
            connection.errorStream?.bufferedReader()?.readText()
                ?: "Error desconocido: $responseCode"
        }

        connection.disconnect()
        return@withContext response.trim()
    } catch (e: Exception) {
        "Error de conexión: ${e.localizedMessage}"
    }
}

/**
 * Verifica la existencia de un usuario a través de su correo electrónico.
 */
suspend fun userExists(email: String): Boolean {
    val url = "${URL_BASE}/api/userExists?email=${
        URLEncoder.encode(
            email.trim(), "UTF-8"
        )
    }"
    val response = makeApiRequest(url)
    return response.toBooleanStrictOrNull() ?: false
}

/**
 * Realiza un intento de inicio de sesión con los datos proporcionados por el usuario.
 */
suspend fun loginUser(email: String, password: String): String {
    val url = "${URL_BASE}/api/login"
    val json = JSONObject().apply {
        put("email", email)
        put("password", password)
    }
    return makeApiRequest(url, "POST", json)
}

/**
 * Mediante los datos introducidos por el usuario, lo registra en la base de datos para que tenga acceso futuro a la aplicación.
 */
suspend fun registerUser(email: String, password: String): String {
    val url = "${URL_BASE}/api/register"
    val json = JSONObject().apply {
        put("email", email)
        put("password", password)
    }
    return makeApiRequest(url, "POST", json)
}

/**
 * Obtiene la información del usuario con el correo proporcionado.
 */
suspend fun getUserInfo(email: String): Result<Map<String, Any>> {
    val url = "${URL_BASE}/api/userInfo?email=${
        URLEncoder.encode(
            email.trim(), "UTF-8"
        )
    }"
    val response = makeApiRequest(url)

    return try {
        val jsonResponse = JSONObject(response)
        val username = jsonResponse.getString("username")
        val fechaRegistro = jsonResponse.getString("fecha_registro")
        Result.success(mapOf("username" to username, "fecha_registro" to fechaRegistro))
    } catch (e: Exception) {
        Log.e("PROFILE", "Excepción en getUserInfo: ${e.message}", e)
        Result.failure(e)
    }
}

/**
 * Obtiene la lista de canciones disponibles desde el servidor, filtradas por género si se proporciona.
 */
suspend fun getServerSongs(genre: String = "null"): Result<List<Album>> {
    val url = "${URL_BASE}/api/songs?genre=${
        URLEncoder.encode(
            genre.trim(), "UTF-8"
        )
    }"

    val response = makeApiRequest(url)

    return try {
        val jsonResponse = JSONArray(response)

        val songList = List(jsonResponse.length()) { index ->
            val id = jsonResponse.getJSONObject(index).getInt("song_id")
            val title = jsonResponse.getJSONObject(index).getString("title")
            val artist = jsonResponse.getJSONObject(index).getString("artist")
            val duration = jsonResponse.getJSONObject(index).getDouble("duration")
            val url = jsonResponse.getJSONObject(index).getString("url")
            Album(
                id = id, name = title, author = artist, duration = duration, url = url
            )
        }

        Result.success(songList)
    } catch (jsonException: JSONException) {
        Log.e(
            "SOUND_BEAT",
            "Error procesando el JSON de canciones: ${jsonException.message}",
            jsonException
        )
        Result.failure(jsonException)
    } catch (exception: Exception) {
        Log.e("SOUND_BEAT", "Error obteniendo canciones: ${exception.message}", exception)
        Result.failure(exception)
    }
}

//fun getUserPlaylists(): Result<List<Playlist>> {
//    return Result<List<Playlist>>
//}

/**
 * Crea una nueva playlist para el usuario de la aplicación.
 *
 * @param songs: Colección de canciones que se agregarán a la playlist.
 * @param name: Nombre de la playlist a crear o modificar.
 */
suspend fun createPlaylist(playlistName: String, userEmail: String): String {
    val url = "${URL_BASE}/api/createPlaylist?name=${playlistName}&email=${userEmail}"
    return makeApiRequest(url, method = "POST")
}

/**
 * Agrega una o varias canciones a una playlist.
 * @param playlistId: Identificador de la playlist.
 * @param songIds: Colección que contiene los identificadores de las canciones a agregar.
 */
suspend fun addSongsToPlaylist(playlistId: Int, songIds: List<Int>): String {
    val jsonBody = JSONObject().apply {
        put("playlist_id", playlistId)
        put("song_ids", JSONArray(songIds))
    }

    val url = "${URL_BASE}/api/playlists/add-songs"
    return makeApiRequest(url, method = "POST", jsonBody = jsonBody)
}


