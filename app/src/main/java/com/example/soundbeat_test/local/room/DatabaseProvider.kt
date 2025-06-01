package com.example.soundbeat_test.local.room

import android.content.Context
import androidx.room.Room
import com.example.soundbeat_test.local.room.dao.PlaylistDao
import com.example.soundbeat_test.local.room.dao.PlaylistSongDao
import com.example.soundbeat_test.local.room.dao.SongDao

object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instancia = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "database_local.db"
            ).build()
            INSTANCE = instancia
            instancia
        }
    }

    fun getPlaylistDao(context: Context): PlaylistDao {
        return this.getDatabase(context).playlistDao()!!
    }

    fun getSongDao(context: Context): SongDao {
        return this.getDatabase(context).songDao()!!
    }

    fun getPlaylistSongDao(context: Context): PlaylistSongDao {
        return this.getDatabase(context).playlistSongDao()!!
    }
}