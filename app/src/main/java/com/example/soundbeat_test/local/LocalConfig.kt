package com.example.soundbeat_test.local

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.soundbeat_test.R
import com.example.soundbeat_test.data.Album
import java.io.File

object LocalConfig {
    private const val DEFAULT_PATH = "/storage/emulated/0/Music"
    private var currentPath: String? = null

    fun init(context: Context) {
        val prefs = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        val directory = prefs.getString("directory", DEFAULT_PATH) ?: DEFAULT_PATH
        currentPath = directory
    }

    @OptIn(UnstableApi::class)
    fun getMusicDirectory(): String {
        Log.d("%%%", "$currentPath")
        return currentPath ?: DEFAULT_PATH
    }

    fun setMusicDirectory(context: Context, newPath: String) {
        currentPath = Uri.parse(newPath).toString()
        val prefs = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        prefs.edit().putString("music_path", newPath).apply()
    }

    @OptIn(UnstableApi::class)
    fun listLocalAlbums(): List<Album> {
        val musicDirectory = File(getMusicDirectory())

        if (!musicDirectory.exists() || !musicDirectory.isDirectory) {
            Log.d("API-LOCAL", "directory not found: ${musicDirectory.absolutePath}")
            return emptyList()
        }

        val mp3Files = musicDirectory.listFiles { file ->
            file.isFile && file.extension.equals("mp3", ignoreCase = true)
        } ?: return emptyList()

        return mp3Files.mapIndexed { index, file ->
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(file.absolutePath)
                val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ?: file.nameWithoutExtension
                val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                    ?: "Unknown Artist"
                val durationMs =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLongOrNull() ?: 0L

                Album(
                    id = index,
                    title = title,
                    author = artist,
                    genre = listOf("Other"),
                    imageResId = R.drawable.default_vinyl,
                    url = "file://${file.absolutePath}",
                    duration = durationMs / 1000.0,
                    isLocal = true
                )
            } catch (e: Exception) {
                Log.d("API-LOCAL", "failed to retrieve metadata from file: ${file.name}")
                Album(
                    id = index,
                    title = file.nameWithoutExtension,
                    author = "Unknown",
                    genre = listOf("Other"),
                    imageResId = R.drawable.default_vinyl,
                    url = "file://${file.absolutePath}",
                    duration = 0.0,
                    isLocal = true
                )
            } finally {
                retriever.release()
            }
        }
    }

}