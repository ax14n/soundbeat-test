package com.example.soundbeat_test.local

import android.media.MediaMetadataRetriever
import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.soundbeat_test.R
import com.example.soundbeat_test.data.Album
import java.io.File

/**
 * Ruta donde se buscarán las canciones dentro del sistema.
 */
const val URI_BASE = "/storage/emulated/0/Music"

@OptIn(UnstableApi::class)
fun listLocalAlbums(): List<Album> {
    val musicDirectory = File(URI_BASE)

    if (!musicDirectory.exists() || !musicDirectory.isDirectory) {
        Log.d("apiLocal", "[LocalScan] Directory not found: $URI_BASE")
        return emptyList()
    }

    val mp3Files = musicDirectory.listFiles { file ->
        file.isFile && file.extension.equals("mp3", ignoreCase = true)
    } ?: return emptyList()

    val albums = mp3Files.mapIndexed { index, file ->
        val retriever = MediaMetadataRetriever()
        val album: Album = try {
            retriever.setDataSource(file.absolutePath)

            val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                ?: file.nameWithoutExtension
            val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                ?: "Unknown Artist"
            val durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                ?.toLongOrNull() ?: 0L

            Album(
                id = index,
                name = title,
                author = artist,
                genre = emptyList(),
                imageResId = R.drawable.default_vinyl,
                url = "file://${file.absolutePath}",
                duration = durationMs / 1000.0,
                isLocal = true
            )
        } catch (e: Exception) {
            println("[LocalScan] Fallo al leer los meta-datos de: ${file.name}")
            Album(
                id = index,
                name = file.nameWithoutExtension,
                author = "Unknown",
                genre = emptyList(),
                imageResId = R.drawable.default_vinyl,
                url = "file://${file.absolutePath}",
                duration = 0.0,
                isLocal = true
            )
        } finally {
            retriever.release()
        }
        album
    }

    return albums
}



