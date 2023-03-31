package com.example.inrow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GameRecord::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        GameDatabase::class.java,
                        name = "game_DB").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    abstract fun getGameDatabaseDao(): GameDatabaseDao
}