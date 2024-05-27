package com.optic.moveon.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.*
import com.optic.moveon.DefaultApp
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityResidenceBinding
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.LocalResidence
import com.optic.moveon.model.entities.Residence
import com.optic.moveon.viewmodel.MainViewModel
import com.optic.moveon.viewmodel.MainViewModelFactory
import com.squareup.picasso.Picasso

class ResidenceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResidenceBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var dbref: DatabaseReference
    private var residence: Residence? = null
    private lateinit var viewModel: MainViewModel
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResidenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViewModel()
        initializeFirebaseAnalytics()

        val residenceId = intent.getStringExtra("residence_name")
        setupButtonListeners()

        residenceId?.let {
            fetchResidenceData(it)
        } ?: run {
            Log.i("ResidenceActivity", "Residence ID is null")
        }

        binding.favorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon(isFavorite)
            viewModel.updateResidence(LocalResidence(firebaseId = residence?.id, imageUrl = residence?.image, favorite = isFavorite))
            handleFavoriteEvent(isFavorite)
        }
    }

    private fun initializeViewModel() {
        val universityDao = (application as DefaultApp).localdb.localUniversityDao()
        val residenceDao = (application as DefaultApp).localdb.localResidenceDao()
        viewModel = ViewModelProvider(this, MainViewModelFactory(universityDao, residenceDao)).get(MainViewModel::class.java)
        Log.d("ResidenceActivity", "UID guardado: ${UserSessionManager.getUid()}")
    }

    private fun initializeFirebaseAnalytics() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    private fun setupButtonListeners() {
        binding.button1.setOnClickListener { showInfoText("Info: Located steps away from the subway and extremely well connected with public transport to all of Spain’s capital. Node Madrid is a transformed warehouse with very spacious communal spaces.") }
        binding.button2.setOnClickListener { showInfoText("Calle Sanchez Preciado 12, Madrid, 28039 - Distance to Universidad Politécnica de Madrid: 7 mins by car") }
        binding.button3.setOnClickListener { showInfoText("Individual bedrooms - Private bathrooms.") }
        binding.button4.setOnClickListener { showInfoText("Wi-Fi, Water, Electricity, Refrigerator, Cinema Room, Library") }
        binding.button5.setOnClickListener { showInfoText("€785/Month") }
    }

    private fun showInfoText(infoText: String) {
        binding.infoTextView2.text = infoText
        binding.infoTextView2.visibility = View.VISIBLE
        binding.infoTextView1.visibility = View.GONE
    }

    private fun fetchResidenceData(residenceId: String) {
        dbref = FirebaseDatabase.getInstance().getReference("Residences")
        val query = dbref.orderByChild("name").equalTo(residenceId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstResidenceSnapshot = snapshot.children.firstOrNull()
                    residence = firstResidenceSnapshot?.getValue(Residence::class.java)
                    displayResidenceData()
                } else {
                    Log.w("ResidenceActivity", "Residence not found: $residenceId")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ResidenceActivity", "Database error: ${error.message}")
            }
        })
    }

    private fun displayResidenceData() {
        residence?.let { res ->
            binding.residenceName.text = res.name
            binding.residenceLocation.text = res.country
            res.image?.let { imageUrl ->
                Picasso.get().load(imageUrl).into(binding.headerImage)
            }
            initializeFavoriteState(res)

            binding.sitio.setOnClickListener {
                val url = res.siteUrl ?: res.description
                url?.let {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
                } ?: run {
                    Toast.makeText(this, "URL no disponible", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initializeFavoriteState(residence: Residence) {
        viewModel.getResidenceById(residence.id)
        viewModel.localSingleResidence.observe(this) { localResidence ->
            isFavorite = localResidence.favorite ?: false
            updateFavoriteIcon(isFavorite)
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.favorite.setImageResource(if (isFavorite) R.drawable.heartfull else R.drawable.heart)
    }

    private fun handleFavoriteEvent(isFavorite: Boolean) {
        if (isFavorite) {
            Toast.makeText(this, "Residencia agregada a favoritos", Toast.LENGTH_SHORT).show()
            residenceFirebaseEvent("add_favorite", residence?.name ?: "Unknown Residence")
        } else {
            Toast.makeText(this, "Residencia removida de favoritos", Toast.LENGTH_SHORT).show()
            residenceFirebaseEvent("remove_favorite", residence?.name ?: "Unknown Residence")
        }
    }

    private fun residenceFirebaseEvent(eventName: String, residenceName: String?) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, residenceName)
            putString(FirebaseAnalytics.Param.ITEM_NAME, residenceName)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    fun recommendResidences() {
        // getTopUniversities { universities ->
        //     for (university in universities) {
        //         // Supongamos que tenemos una función que obtiene residencias cercanas a una universidad
        //         getNearbyResidences(university) { residences ->
        //             // Aquí puedes mostrar las residencias recomendadas al usuario
        //             for (residence in residences) {
        //                 Log.d("Recommendation", "Recommended Residence: ${residence.name} near ${university.name}")
        //             }
        //         }
        //     }
        // }
    }
}