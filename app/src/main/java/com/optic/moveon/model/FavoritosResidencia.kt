package com.optic.moveon.model

import android.util.LruCache
import com.optic.moveon.model.entities.Residence
import com.optic.moveon.model.entities.University

object FavoritosResidencia {
    private val cacheSize = 3 // Almacena hasta 3 universidades favoritas
    private val lruCache = LruCache<Int, Residence>(cacheSize)

    fun addFavorite(residence: Residence) {
        residence.id?.let {
            lruCache.put(it, residence)
        }
    }

    fun getFavorite(id: Int): Residence? {
        return lruCache.get(id)
    }

    fun removeFavorite(id: Int) {
        lruCache.remove(id)
    }

    fun getAllFavorites(): List<Residence> {
        return lruCache.snapshot().values.toList()
    }
}