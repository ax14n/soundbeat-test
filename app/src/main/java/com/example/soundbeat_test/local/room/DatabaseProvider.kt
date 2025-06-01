package com.example.soundbeat_test.local.room

import android.content.Context
import androidx.room.Room

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

}