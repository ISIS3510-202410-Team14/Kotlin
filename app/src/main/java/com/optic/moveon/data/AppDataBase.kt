package com.optic.moveon.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.optic.moveon.model.entities.LocalUniversity


@Database(entities = [LocalUniversity::class], version = 1)
abstract class AppDataBase : RoomDatabase(){
    abstract fun localUniversityDao():UniversityDAO
}