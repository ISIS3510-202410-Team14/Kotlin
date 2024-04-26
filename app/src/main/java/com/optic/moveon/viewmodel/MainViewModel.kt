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
import com.optic.moveon.model.entities.University
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class MainViewModel(var dao: UniversityDAO) : ViewModel() {
    var universityLiveData : MutableLiveData<List<University>> = MutableLiveData()
    fun getUniversities()=viewModelScope.launch{
        fetchUniversities {
        universityLiveData.value = it
        }
    }



    suspend fun fetchUniversities(success:(List<University>)->Unit){
        var dbref = FirebaseDatabase.getInstance().getReference("Universities")
        withContext(Dispatchers.IO){
            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var universityList = arrayListOf<University>()
                        for (userSnapshot in snapshot.children) {
                            val university = userSnapshot.getValue(University::class.java)
                            //val localResult = dao.getUniversityById(userSnapshot.key!!)
                            universityList.add(university!!)
                            //if (localResult == null){
                            //dao.insertUniversity(LocalUniversity(firebaseId = userSnapshot.key!!, imageUrl = university.image, favorite = false, uid = 0))
                            //}
                        }
                        success(universityList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("Prueba", "onCancelled: $error")
                }
            })
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