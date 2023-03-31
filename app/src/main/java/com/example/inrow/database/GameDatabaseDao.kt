package com.example.inrow.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameDatabaseDao {

    @Insert
    fun insert(record: GameRecord)

    @Query("SELECT * FROM Games ORDER BY id DESC")
    fun getAll(): LiveData<List<GameRecord>>
}