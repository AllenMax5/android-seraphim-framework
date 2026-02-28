package com.seraphim.delicacies.shared.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<DelicaciesDatabase> {
    val dbFile = context.getDatabasePath("delicacies.db")
    return Room.databaseBuilder<DelicaciesDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}
