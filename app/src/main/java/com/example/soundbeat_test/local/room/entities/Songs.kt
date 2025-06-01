package com.example.soundbeat_test.local.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Songs")
data class Song(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "song_id") val songId: Int = 0,

    val title: String,

    val artist: String,

    val duration: Int,

    val url: String,

    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis() // Timestamp en milisegundos.
)