package com.optic.moveon.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.optic.moveon.DefaultApp
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityResidenceBinding
import com.optic.moveon.model.FavoritesCache
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.LocalResidence
import com.optic.moveon.model.entities.LocalUniversity
import com.optic.moveon.model.entities.Requerimiento
import com.optic.moveon.model.entities.Residence
import com.optic.moveon.model.entities.University
import com.optic.moveon.model.entities.UniversityProperties
import com.optic.moveon.viewmodel.MainViewModel
import com.optic.moveon.viewmodel.MainViewModelFactory
import com.squareup.picasso.Picasso

class ResidenceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResidenceBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var dbref: DatabaseReference
    private lateinit var dbr: DatabaseReference

    private lateinit var userRecyclerview: RecyclerView
    private lateinit var adapterResidence: AdapterResidence
    private var residence: Residence? = null
    private lateinit var viewModel: MainViewModel
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResidenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val universityDao = (application as DefaultApp).localdb.localUniversityDao()
        val residenceDao = (application as DefaultApp).localdb.localResidenceDao()
        viewModel = ViewModelProvider(this, MainViewModelFactory(universityDao, residenceDao)).get(MainViewModel::class.java)
        val uid = UserSessionManager.getUid()
        Log.d("ResidenceActivity", "UID guardado: $uid")


        val residenceId = intent.getStringExtra("residence_name")

        binding.button1.setOnClickListener {
            // Mostrar la información de la universidad si los datos no son nulos
            binding.infoTextView1.text = "Info: Located steps away from the subway and extremely well connected with public transport to all of Spain’s capital. Node Madrid is a transformed warehouse with very spacious communal spaces. "
            binding.infoTextView1.visibility = View.VISIBLE
            binding.infoTextView2.visibility = View.GONE
        }

        // Configurar los OnClickListeners para los otros botones
        binding.button2.setOnClickListener {
            binding.infoTextView2.text = "Calle Sanchez Preciado 12, Madrid, 28039 - " +
                    "Distance to Universidad Politécnica de Madrid: 7 mins by car"
            binding.infoTextView2.visibility = View.VISIBLE
            binding.infoTextView1.visibility = View.GONE
        }

        binding.button3.setOnClickListener {
            binding.infoTextView2.text = "Individual bedrooms - Private bathrooms."
            binding.infoTextView2.visibility = View.VISIBLE
            binding.infoTextView1.visibility = View.GONE
        }

        binding.button4.setOnClickListener {
            binding.infoTextView2.text = "Wi-Fi, Water, Electricity, Refrigerator, Cinema Room, Library"
            binding.infoTextView2.visibility = View.VISIBLE
            binding.infoTextView1.visibility = View.GONE
        }

        binding.button5.setOnClickListener {
            binding.infoTextView2.text = "€785/Month"
            binding.infoTextView2.visibility = View.VISIBLE
            binding.infoTextView1.visibility = View.GONE
        }

        // Inicializar Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        println("jejejeee")
        println(residenceId)
        Log.i("ResidenceActivity", residenceId ?: "Residence ID is null")
        println(residenceId)

        binding.favorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon(isFavorite)
            viewModel.updateResidence(LocalResidence(firebaseId = residence?.id, imageUrl = residence?.image, favorite = isFavorite))

            // Registra el evento dependiendo de si es favorito o no
            if (isFavorite) {
                Toast.makeText(this, "Residencia agregada a favoritos", Toast.LENGTH_SHORT).show()
                residenceFirebaseEvent("add_favorite", residence?.name ?: "Unknown Residence")
                Log.d("analytics","funcionoAgregar")
            } else {
                Toast.makeText(this, "Residencia removida de favoritos", Toast.LENGTH_SHORT).show()
                residenceFirebaseEvent("remove_favorite", residence?.name ?: "Unknown Residence")
                Log.d("analytics","funcionoEliminar")
            }
        }

        dbref = FirebaseDatabase.getInstance().getReference("Residences")


        val query = dbref.orderByChild("name").equalTo(residenceId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstResidenceSnapshot = snapshot.children.firstOrNull()
                    residence = firstResidenceSnapshot?.getValue(Residence::class.java)
                    binding.residenceName.text = residence?.name ?: ""
                    binding.residenceLocation.text = residence?.country
                    residence?.image?.let {
                        Picasso.get().load(it).into(binding.headerImage)
                    }
                    intFavorite(residence)

                    binding.sitio.setOnClickListener {
                        val url = residence?.siteUrl ?: residence?.description
                        if (url != null && url.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@ResidenceActivity, "URL no disponible", Toast.LENGTH_SHORT).show()
                        }
                    }

                    //binding.chat.setOnClickListener {
                        //val intent = Intent(this@ResidenceActivity, ChatActivity2::class.java)
                        //intent.putExtra("name", residence?.name)
                        //startActivity(intent)
                   // }


                } else {
                    // Si no se encuentra ninguna universidad con el ID proporcionado, mostrar un mensaje de error
                    // (aquí puedes manejar el caso de que la universidad no se encuentre)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores de base de datos, si es necesario
                // (aquí puedes manejar los errores de Firebase)
            }
        })





    }

    private fun intFavorite(residence: Residence?) {
        viewModel.getResidenceById(residence?.id)
        viewModel.localSingleResidence.observe(this){
            isFavorite = it.favorite ?: false
            updateFavoriteIcon(it.favorite ?: false)
        }

    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        Log.d("pruebas", "updateFavoriteIcon: $isFavorite")
        if (isFavorite == true) {
            binding.favorite.setImageResource(R.drawable.heartfull)  // Asume que tienes un drawable que representa "favorito"
        } else {
            binding.favorite.setImageResource(R.drawable.heart) // Asume que tienes un drawable que representa "no favorito"
        }
    }

    private fun residenceFirebaseEvent(eventName: String, residenceName: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, residenceName)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, residenceName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        firebaseAnalytics.logEvent(eventName, bundle)
    }

}