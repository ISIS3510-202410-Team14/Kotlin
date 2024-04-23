package com.optic.moveon.model

import android.util.LruCache
import com.optic.moveon.model.entities.University

object FavoritesCache {
    private val cacheSize = 3 // Almacena hasta 3 universidades favoritas
    private val lruCache = LruCache<Int, University>(cacheSize)

    fun addFavorite(university: University) {
        university.id?.let {
            lruCache.put(it, university)
        }
    }

    fun getFavorite(id: Int): University? {
        return lruCache.get(id)
    }

    fun removeFavorite(id: Int) {
        lruCache.remove(id)
    }

    fun getAllFavorites(): List<University> {
        return lruCache.snapshot().values.toList()
    }
}
