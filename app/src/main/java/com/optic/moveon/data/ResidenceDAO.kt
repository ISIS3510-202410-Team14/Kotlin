package com.optic.moveon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.optic.moveon.model.entities.LocalResidence
@Dao
interface ResidenceDAO {
    @Query("Select * from localresidence where firebaseId = :cad")
    fun getResidenceById(cad:Int):LocalResidence?
    @Insert
    fun insertResidence(localResidence: LocalResidence)
    @Query("Select * from localresidence")
    fun geListResidence():List<LocalResidence>?
    @Query("Update localresidence set favorite = :favorite where firebaseId = :firebaseId")
    fun updateResidenceFavorite(firebaseId:Int,favorite :Boolean)
    @Query("Delete from localresidence")
    fun deleteResidences()
}