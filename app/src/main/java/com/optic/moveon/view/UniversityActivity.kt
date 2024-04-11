package com.optic.moveon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityUniversityBinding
import com.optic.moveon.model.entities.UniversityProperties

class UniversityActivity : AppCompatActivity() , AdapterUniversity.UniversityPropertiesListener {

    private lateinit var binding: ActivityUniversityBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var adapterUniversity: AdapterUniversity
    private val itemList = arrayListOf<UniversityProperties>(UniversityProperties(1, "Agreement", "Texto 3", false),
        UniversityProperties(2, "Faculty", "Texto 4", false))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUniversityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        initRecyclerView()

        binding.requirements.setOnClickListener {
            logFirebaseEvent("ingreso_a_requirements")
            val intent = Intent(this, RequirementActivity::class.java)
            startActivity(intent)
        }


    }

    private fun initRecyclerView() {
        adapterUniversity = AdapterUniversity(this)
        binding.listDescription.apply {
            layoutManager = LinearLayoutManager(this@UniversityActivity)
            adapter = adapterUniversity
        }
        adapterUniversity.setItemList(itemList)
    }

    private fun logFirebaseEvent(eventName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, eventName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    override fun onUniversityClick(item: UniversityProperties, position: Int) {
        val actual = itemList.get(position)
        itemList.set(position, actual.copy(selected = !actual.selected))
        adapterUniversity.setItemList(itemList)
        Log.d("pruebas","aqui")
    }
}

