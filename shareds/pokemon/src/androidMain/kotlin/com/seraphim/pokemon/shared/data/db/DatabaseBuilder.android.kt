package com.seraphim.pokemon.shared.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<PokemonDatabase> {
    val dbFile = context.getDatabasePath("pokemon.db")
    return Room.databaseBuilder<PokemonDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
}
