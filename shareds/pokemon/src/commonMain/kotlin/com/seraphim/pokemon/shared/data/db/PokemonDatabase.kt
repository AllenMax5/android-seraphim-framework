package com.seraphim.pokemon.shared.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.seraphim.pokemon.shared.data.db.dao.EvolutionChainDao
import com.seraphim.pokemon.shared.data.db.dao.PokemonDetailDao
import com.seraphim.pokemon.shared.data.db.dao.PokemonFavoriteDao
import com.seraphim.pokemon.shared.data.db.dao.PokemonListDao
import com.seraphim.pokemon.shared.data.db.dao.PokemonSpeciesDao
import com.seraphim.pokemon.shared.data.db.dao.TypeDao
import com.seraphim.pokemon.shared.data.db.entity.EvolutionChainEntity
import com.seraphim.pokemon.shared.data.db.entity.PokemonDetailEntity
import com.seraphim.pokemon.shared.data.db.entity.PokemonFavoriteEntity
import com.seraphim.pokemon.shared.data.db.entity.PokemonListEntity
import com.seraphim.pokemon.shared.data.db.entity.PokemonSpeciesEntity
import com.seraphim.pokemon.shared.data.db.entity.TypeEntity

@Database(
    entities = [
        PokemonListEntity::class,
        PokemonDetailEntity::class,
        PokemonSpeciesEntity::class,
        EvolutionChainEntity::class,
        TypeEntity::class,
        PokemonFavoriteEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(PokemonDatabaseConstructor::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonListDao(): PokemonListDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
    abstract fun pokemonSpeciesDao(): PokemonSpeciesDao
    abstract fun evolutionChainDao(): EvolutionChainDao
    abstract fun typeDao(): TypeDao
    abstract fun pokemonFavoriteDao(): PokemonFavoriteDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object PokemonDatabaseConstructor : RoomDatabaseConstructor<PokemonDatabase>
