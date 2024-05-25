package com.optic.moveon.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.optic.moveon.model.entities.LocalUniversity
import com.optic.moveon.model.entities.LocalResidence

@Database(entities = [LocalUniversity::class, LocalResidence::class], version = 2)
abstract class AppDataBase : RoomDatabase() {
    abstract fun localUniversityDao(): UniversityDAO
    abstract fun localResidenceDao(): ResidenceDAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Añadir aquí tu lógica de migración
                database.execSQL("CREATE TABLE IF NOT EXISTS `LocalResidence` (`uid` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `firebaseId` INTEGER, `imageUrl` TEXT, `favorite` INTEGER)")
            }
        }
    }
}