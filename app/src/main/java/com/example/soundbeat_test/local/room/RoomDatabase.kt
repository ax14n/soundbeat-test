package com.example.soundbeat_test.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.soundbeat_test.local.room.dao.PlaylistDao
import com.example.soundbeat_test.local.room.dao.PlaylistSongDao
import com.example.soundbeat_test.local.room.dao.SongDao
import com.example.soundbeat_test.local.room.entities.Playlist
import com.example.soundbeat_test.local.room.entities.PlaylistSong
import com.example.soundbeat_test.local.room.entities.Song

/**
 * Clase AppDatabase para contener la base de datos. AppDatabase define la configuración
 * de la base de datos y sirve como el punto de acceso principal de la app a los datos persistentes.
 *
 * `https://developer.android.com/training/data-storage/room?hl=es-419#java`
 */
@Database(entities = [Song::class, Playlist::class, PlaylistSong::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao?
    abstract fun playlistDao(): PlaylistDao?
    abstract fun playlistSongDao(): PlaylistSongDao?
}
