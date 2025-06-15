package com.example.soundbeat_test.network

import android.util.Log
import com.example.soundbeat_test.R
import com.example.soundbeat_test.data.Album
import com.example.soundbeat_test.data.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

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
            OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                writer.write(jsonBody.toString())
            }
        }

        val responseCode = connection.responseCode
        Log.d("API_REQUEST", "Código de estado: $responseCode")

        val response = if (responseCode == HttpURLConnection.HTTP_OK) {
            connection.inputStream.bufferedReader().readText()
        } else {
            connection.errorStream?.bufferedReader()?.readText()
                ?: "Error desconocido: $responseCode"
        }

        Log.d("API_REQUEST", "Respuesta del servidor: $response")

        connection.disconnect()
        return@withContext response.trim()
    } catch (e: Exception) {
        Log.e("API_REQUEST", "${e.message}")
        ""
    }
}

/**
 * Verifica la existencia de un usuario a través de su correo electrónico.
 */
suspend fun userExists(email: String): Boolean {
    val url = "${ServerConfig.getBaseUrl()}/api/userExists?email=${
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
    val url = "${ServerConfig.getBaseUrl()}/api/login"
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
    val url = "${ServerConfig.getBaseUrl()}/api/register"
    val json = JSONObject().apply {
        put("email", email)
        put("password", password)
    }
    return makeApiRequest(url, "POST", json)
}

/**
 * Agrega la canción escuchada a la lista de favoritos.
 * @param email email de la cuenta donde agregar la canción
 * @param album album de donde obtener la información.
 */
suspend fun addSongToFavorites(email: String, album: Album) {
    if (!album.isLocal) {

        Log.d("API", "trying to add song with id ${album.id} to $email favorite songs")
        val url = "${ServerConfig.getBaseUrl()}/api/favorites/addFavorite"
        val json = JSONObject().apply {
            put("email", email)
            put("songId", album.id)
        }

        makeApiRequest(url, "POST", json)
    }
}

/**
 * Elimina la canción escuchada a la lista de favoritos.
 * @param email email de la cuenta donde agregar la canción
 * @param album album de donde obtener la información.
 */
suspend fun removeSongFromFavorites(email: String, album: Album) {
    if (!album.isLocal) {

        Log.d("API", "trying to add song with id ${album.id} to $email favorite songs")
        val url = "${ServerConfig.getBaseUrl()}/api/favorites/deleteFavorite"
        val json = JSONObject().apply {
            put("email", email)
            put("songId", album.id)
        }

        makeApiRequest(url, "POST", json)
    }
}

suspend fun isFavorite(email: String, album: Album): String {
    if (!album.isLocal) {

        val url = "${ServerConfig.getBaseUrl()}/api/favorites/isFavorite?email=${
            URLEncoder.encode(
                email.trim(), "UTF-8"
            )
        }&songId=${album.id}"
        Log.d(
            "API", "trying receive if song with id ${album.id} in $email favorite songs. URL: $url"
        )
        val result = makeApiRequest(url)
        Log.d(
            "API", "${album.id} in $email favorite songs? ${if (result == "true") "YES" else "NO"} "
        )
        return result
    }
    return "false"
}

suspend fun setUsername(email: String, newUsername: String) {
    Log.d("API", "trying to set a new username: $newUsername to account named $email")
    val url = "${ServerConfig.getBaseUrl()}/api/configurations/changeUsername"
    val json = JSONObject().apply {
        put("email", email)
        put("newUsername", newUsername)
    }

    makeApiRequest(url, "POST", json)
}

suspend fun setEmail(email: String, newEmail: String) {
    Log.d("API", "trying to set a new email: $newEmail to account named $email")
    val url = "${ServerConfig.getBaseUrl()}/api/configurations/changeEmail"
    val json = JSONObject().apply {
        put("email", email)
        put("newEmail", newEmail)
    }

    makeApiRequest(url, "POST", json)
}

suspend fun setPassword(email: String, newPassword: String) {
    Log.d("API", "trying to set a new password to account named $email")
    val url = "${ServerConfig.getBaseUrl()}/api/configurations/changePassword"
    val json = JSONObject().apply {
        put("email", email)
        put("newPassword", newPassword)
    }

    makeApiRequest(url, "POST", json)
}


/**
 * Obtiene la información del usuario con el correo proporcionado.
 */
suspend fun getUserInfo(email: String): Result<Map<String, Any>> {
    val url = "${ServerConfig.getBaseUrl()}/api/userInfo?email=${
        URLEncoder.encode(
            email.trim(), "UTF-8"
        )
    }"
    val response = makeApiRequest(url)

    return try {
        val jsonResponse = JSONObject(response)
        Log.d("API_RESPONSE", response)

        val username = jsonResponse.getString("username")
        val dateJoined = jsonResponse.getString("dateJoined")
        Result.success(mapOf("username" to username, "dateJoined" to dateJoined))
    } catch (e: Exception) {
        Log.e("API", "exception in getUserInfo: ${e.message}", e)
        Result.failure(e)
    }
}

/**
 * Recupera las playlists asociadas a un usuario dado su email.
 *
 * Esta función realiza una solicitud HTTP a la API para obtener todas las playlists
 * correspondientes al email proporcionado. El resultado se procesa como un JSONArray,
 * y se convierte en una lista de mapas clave-valor con los datos de cada playlist.
 *
 * @param email Dirección de correo electrónico del usuario cuyas playlists se quieren obtener.
 * @return Un [Result] que contiene una lista de mapas con la información de cada playlist
 *         si la operación fue exitosa, o un error si algo falló en la petición o el procesamiento del JSON.
 */
suspend fun getUserPlaylists(email: String): Result<List<Playlist>> {
    val url = "${ServerConfig.getBaseUrl()}/api/userPlaylists?email=${
        URLEncoder.encode(
            email.trim(), "UTF-8"
        )
    }"
    val response = makeApiRequest(url)

    return try {
        val jsonArray = JSONArray(response)
        Log.d("API_RESPONSE", response)

        val playlists = List(jsonArray.length()) { index ->
            val jsonObj = jsonArray.getJSONObject(index)
            Playlist(
                id = jsonObj.getInt("playlist_id"),
                name = jsonObj.getString("name"),
                songs = emptySet() // No vienen las canciones en esta llamada
            )
        }

        Result.success(playlists)
    } catch (jsonEx: JSONException) {
        Log.e(
            "API", "failed to parse user playlists JSON response: ${jsonEx.message}", jsonEx
        )
        Result.failure(jsonEx)
    } catch (ex: Exception) {
        Log.e("API", "unexpected error while fetching user playlists: ${ex.message}", ex)
        Result.failure(ex)
    }
}

/**
 * Obtiene la lista de canciones asociadas a una playlist específica desde el servidor.
 *
 * Realiza una petición GET al endpoint `/api/getPlaylistSongs` proporcionando el ID de la playlist.
 * Luego, analiza la respuesta JSON y convierte cada entrada en un objeto [Album].
 *
 * @param playlistId ID de la playlist de la cual se quieren obtener las canciones.
 * @return [Result] que contiene una lista de objetos [Album] si la petición fue exitosa,
 *         o una excepción en caso de error (ya sea de red o de análisis de datos).
 */
suspend fun getPlaylistSongs(playlistId: Int): Result<List<Album>> {
    val url = "${ServerConfig.getBaseUrl()}/api/getPlaylistSongs?playlistId=$playlistId"
    val response = makeApiRequest(url)

    return try {
        val jsonArray = JSONArray(response)
        Log.d("API_RESPONSE", response)

        val albumList = List(jsonArray.length()) { index ->
            val json = jsonArray.getJSONObject(index)
            Album(
                id = json.getInt("songId"),
                title = json.getString("title"),
                author = json.getString("artist"),
                genre = listOf(),
                imageResId = R.drawable.default_vinyl,
                url = json.getString("url"),
                duration = json.getDouble("duration")
            )
        }

        Result.success(albumList)
    } catch (ex: Exception) {
        Log.e("API", "failed to fetch songs from playlist: ${ex.message}", ex)
        Result.failure(ex)
    }
}

/**
 * Obtiene la lista de canciones disponibles desde el servidor, filtradas por género si se proporciona.
 */
suspend fun getFavoriteSongs(email: String): Result<Playlist> {
    val url = "${ServerConfig.getBaseUrl()}/api/favorites/getFavorites?email=${
        URLEncoder.encode(
            email.trim(), "UTF-8"
        )
    }"
    val response = makeApiRequest(url)

    return try {
        val jsonResponse = JSONArray(response)

        val songList = List(jsonResponse.length()) { index ->
            val id = jsonResponse.getJSONObject(index).getInt("songId")
            val title = jsonResponse.getJSONObject(index).getString("title")
            val artist = jsonResponse.getJSONObject(index).getString("artist")
            val duration = jsonResponse.getJSONObject(index).getDouble("duration")
            val url = jsonResponse.getJSONObject(index).getString("url")
            val rawGenres = jsonResponse.getJSONObject(index).optJSONArray("genres")
            val genres = mutableListOf<String>()

            if (rawGenres != null && rawGenres.length() > 0) {
                for (i in 0 until rawGenres.length()) {
                    genres.add(rawGenres.getString(i))
                }
            } else {
                genres.add("OTHER")
            }

            Album(
                id = id,
                title = title,
                author = artist,
                duration = duration,
                url = url,
                genre = genres
            )
        }

        val playlist = Playlist(
            id = -1, name = "Favorites", songs = songList.toSet()
        )

        Result.success(playlist)
    } catch (jsonException: JSONException) {
        Log.e(
            "API",
            "error while parsing playlist songs json: ${jsonException.message}",
            jsonException
        )
        Result.failure(jsonException)
    } catch (exception: Exception) {
        Log.e(
            "API", "error while fetching playlist songs: ${exception.message}", exception
        )
        Result.failure(exception)
    }
}

/**
 * Obtiene la lista de canciones disponibles desde el servidor, filtradas por género si se proporciona.
 */
suspend fun getServerSongs(genre: String = "null"): Result<List<Album>> {
    val url = "${ServerConfig.getBaseUrl()}/api/songs?genre=${
        URLEncoder.encode(
            genre.trim(), "UTF-8"
        )
    }"
    Log.d("API_RESPONSE", url)

    val response = makeApiRequest(url)
    Log.d("API_RESPONSE", response)

    return try {
        Log.d("API_RESPONSE", response)
        val jsonResponse = JSONArray(response)

        val songList = List(jsonResponse.length()) { index ->
            val id = jsonResponse.getJSONObject(index).getInt("songId")
            val title = jsonResponse.getJSONObject(index).getString("title")
            val artist = jsonResponse.getJSONObject(index).getString("artist")
            val duration = jsonResponse.getJSONObject(index).getDouble("duration")
            val url = jsonResponse.getJSONObject(index).getString("url")
            val rawGenres = jsonResponse.getJSONObject(index).optJSONArray("genres")
            val genres = mutableListOf<String>()

            if (rawGenres != null && rawGenres.length() > 0) {
                for (i in 0 until rawGenres.length()) {
                    genres.add(rawGenres.getString(i))
                }
            } else {
                genres.add("OTHER")
            }

            Album(
                id = id,
                title = title,
                author = artist,
                duration = duration,
                url = url,
                genre = genres
            )
        }

        Result.success(songList)
    } catch (jsonException: JSONException) {
        Log.e(
            "API",
            "error while parsing playlist songs json: ${jsonException.message}",
            jsonException
        )
        Result.failure(jsonException)
    } catch (exception: Exception) {
        Log.e(
            "API", "error while fetching playlist songs: ${exception.message}", exception
        )
        Result.failure(exception)
    }
}

/**
 * Crea una nueva playlist para el usuario de la aplicación.
 *
 * @param songs: Colección de canciones que se agregarán a la playlist.
 * @param name: Nombre de la playlist a crear o modificar.
 */
suspend fun createPlaylist(
    playlistName: String, userEmail: String, songsId: List<Int>
): String {
    val url = "${ServerConfig.getBaseUrl()}/api/playlists/createPlaylist"

    val jsonBody = JSONObject().apply {
        put("playlist_name", playlistName)
        put("user_email", userEmail)
        put("songs_id", JSONArray(songsId))
    }

    return makeApiRequest(url, method = "POST", jsonBody = jsonBody)
}

/**
 * Elimina una playlits del usuario de la aplicación
 * @param id: Identificador de la playlist.
 */
suspend fun deletePlaylist(id: Int): String {
    val jsonBody = JSONObject().apply {
        put("playlist_id", id)
    }

    return makeApiRequest(
        url = "${ServerConfig.getBaseUrl()}/api/playlists/deletePlaylist",
        method = "POST",
        jsonBody = jsonBody
    )
}

/**
 * Agrega una o varias canciones a una playlist.
 * @param playlistId: Identificador de la playlist.
 * @param albums: Colección que contiene los identificadores de las canciones a agregar.
 */
suspend fun addSongsToRemotePlaylist(playlistId: Int, albums: List<Album?>): String {
    val songsId = albums.map { it?.id }
    val jsonBody = JSONObject().apply {
        put("playlist_id", playlistId)
        put("song_ids", JSONArray(songsId))
    }
    Log.d(
        "API", "trying add songs to an existent playlist. new ids: $songsId"
    )
    val url = "${ServerConfig.getBaseUrl()}/api/playlists/add-songs"
    return makeApiRequest(url, method = "POST", jsonBody = jsonBody)
}

/**
 * Elimina una o varias canciones de una playlist.
 * @param playlistId: Identificador de la playlist.
 * @param songIds: Colección que contiene los identificadores de las canciones a eliminar.
 * */
suspend fun deleteSongsFromPlaylist(playlistId: Int, albums: List<Album?>): String {
    val songsId = albums.map { it?.id }
    val jsonBody = JSONObject().apply {
        put("playlist_id", playlistId)
        put("song_ids", JSONArray(songsId))
    }

    val url = "${ServerConfig.getBaseUrl()}/api/playlists/delete-songs"
    return makeApiRequest(url, method = "POST", jsonBody = jsonBody)
}


