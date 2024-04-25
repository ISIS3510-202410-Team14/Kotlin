package com.optic.moveon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.optic.moveon.model.entities.LocalUniversity
@Dao
interface UniversityDAO {
    @Query("Select * from localuniversity where firebaseId = :cad")
    fun getUniversityById(cad:String):LocalUniversity?
    @Insert
    fun insertUniversity(localUniversity: LocalUniversity)
}