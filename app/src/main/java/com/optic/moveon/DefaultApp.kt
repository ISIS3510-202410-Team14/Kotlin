package com.optic.moveon

import android.app.Application
import androidx.room.Room
import com.optic.moveon.data.AppDataBase

class DefaultApp : Application(){
    lateinit var localdb : AppDataBase
    override fun onCreate() {
        super.onCreate()
        localdb = Room.databaseBuilder(
            this,
            AppDataBase::class.java, "database-name"
        ).build()
    }
}
