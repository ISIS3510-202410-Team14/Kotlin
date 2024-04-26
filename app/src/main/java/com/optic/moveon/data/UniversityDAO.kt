package com.optic.moveon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.optic.moveon.model.entities.LocalUniversity
@Dao
interface UniversityDAO {
    @Query("Select * from localuniversity where firebaseId = :cad")
    fun getUniversityById(cad:Int):LocalUniversity?
    @Insert
    fun insertUniversity(localUniversity: LocalUniversity)
    @Query("Select * from localuniversity")
    fun geListUniversity():List<LocalUniversity>?
    @Query("Update localuniversity set favorite = :favorite where firebaseId = :firebaseId")
    fun updateUniversityFavorite(firebaseId:Int,favorite :Boolean)
    @Query("Delete from localuniversity")
    fun deleteUniversities()
}