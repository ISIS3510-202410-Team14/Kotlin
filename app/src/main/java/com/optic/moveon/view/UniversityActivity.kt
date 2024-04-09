package com.optic.moveon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityUniversityBinding

class UniversityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUniversityBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUniversityBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        binding.requirements.setOnClickListener {
            logFirebaseEvent("ingreso_a_requirements")
            val intent = Intent(this, RequirementActivity::class.java)
            startActivity(intent)
        }


    }

    private fun logFirebaseEvent(eventName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, eventName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        firebaseAnalytics.logEvent(eventName, bundle)
    }



}

