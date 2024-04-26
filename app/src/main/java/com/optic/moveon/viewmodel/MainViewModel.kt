package com.optic.moveon.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.optic.moveon.DefaultApp
import com.optic.moveon.data.UniversityDAO
import com.optic.moveon.model.entities.LocalUniversity
import com.optic.moveon.model.entities.University
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class MainViewModel(var dao: UniversityDAO) : ViewModel() {
    var universityLiveData : MutableLiveData<List<University>> = MutableLiveData()
    var localSingleUniversity : MutableLiveData<LocalUniversity> = MutableLiveData()
    fun getUniversities()=viewModelScope.launch{
        val firebase = FirebaseDatabase.getInstance()
        firebase.setPersistenceEnabled(true)
        var dbref = firebase.getReference("Universities")
        dbref.keepSynced(true)
        val localUniversities = fetchUniversities()
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var universityList = arrayListOf<University>()
                    for (userSnapshot in snapshot.children) {
                        val university = userSnapshot.getValue(University::class.java)
                        val localUniversity = localUniversities?.find { it.firebaseId == university?.id }
                        if (localUniversity == null){
                            insertUniversities(LocalUniversity(firebaseId = university?.id, imageUrl = university?.image, favorite = false))
                        }
                        universityList.add(university!!)
                    }
                    universityLiveData.value = universityList
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Prueba", "onCancelled: $error")
            }
        })
    }

    fun insertUniversities(localUniversity: LocalUniversity) = viewModelScope.launch{
        withContext(Dispatchers.IO){
            dao.insertUniversity(localUniversity)
        }
    }
    suspend fun fetchUniversities(): List<LocalUniversity>? = withContext(Dispatchers.IO){
        dao.geListUniversity()
    }

    fun updateUniversity(localUniversity: LocalUniversity) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            if (localUniversity.firebaseId != null){
                Log.d("pruebas", "updateUniversity: ${localUniversity.favorite}")
                dao.updateUniversityFavorite(localUniversity.firebaseId,localUniversity.favorite ?: false)
            }

        }
    }

    fun getUniversityById(id: Int?) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            if (id != null){
                val university = dao.getUniversityById(id)
                Log.d("pruebas", "getUniversityById: ${university?.favorite}")
                localSingleUniversity.postValue(university)
            }
        }
    }

    fun deleteUniversities() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            dao.deleteUniversities()
        }
    }
}
class MainViewModelFactory(private val dao: UniversityDAO) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}