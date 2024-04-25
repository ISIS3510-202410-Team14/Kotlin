package com.optic.moveon.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class LocalUniversity (
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "firebaseId") val firebaseId: String?,
    @ColumnInfo(name = "imageUrl") val imageUrl: String?,
    @ColumnInfo(name = "favorite") val favorite: Boolean?
)