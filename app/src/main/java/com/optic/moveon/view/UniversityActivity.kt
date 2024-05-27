package com.optic.moveon.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.optic.moveon.DefaultApp
import com.optic.moveon.R
import com.optic.moveon.databinding.ActivityUniversityBinding
import com.optic.moveon.model.UserSessionManager
import com.optic.moveon.model.entities.LocalUniversity

import com.optic.moveon.model.entities.University
import com.optic.moveon.viewmodel.MainViewModel
import com.optic.moveon.viewmodel.MainViewModelFactory
import com.squareup.picasso.Picasso

class UniversityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUniversityBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var dbref: DatabaseReference
    private lateinit var dbr: DatabaseReference

    private lateinit var userRecyclerview: RecyclerView
    private lateinit var adapterUniversity: AdapterUniversity
    private var university: University? = null
    private lateinit var viewModel: MainViewModel
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniversityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val universityDao = (application as DefaultApp).localdb.localUniversityDao()
        val residenceDao = (application as DefaultApp).localdb.localResidenceDao()
        viewModel = ViewModelProvider(this, MainViewModelFactory(universityDao, residenceDao)).get(MainViewModel::class.java)
        val uid = UserSessionManager.getUid()
        Log.d("UniversityActivity", "UID guardado: $uid")


        val universityId = intent.getStringExtra("university_name")

        binding.button1.setOnClickListener {
            // Mostrar la información de la universidad si los datos no son nulos
            binding.infoTextView1.text = "Cupos: 10"
            binding.infoTextView1.visibility = View.VISIBLE
            binding.infoTextView2.visibility = View.GONE
        }

        // Configurar los OnClickListeners para los otros botones
        binding.button2.setOnClickListener {
            binding.infoTextView2.text = "Ingles B2"
            binding.infoTextView2.visibility = View.VISIBLE
            binding.infoTextView1.visibility = View.GONE
        }

        binding.button3.setOnClickListener {
            binding.infoTextView2.text = "One of the most prestigious universities globally, located in Cambridge, Massachusetts, known for its law, business, medical, and engineering faculties among others."
            binding.infoTextView2.visibility = View.VISIBLE
            binding.infoTextView1.visibility = View.GONE
        }

        binding.button4.setOnClickListener {
            binding.infoTextView2.text = "Promedio: 4.8"
            binding.infoTextView2.visibility = View.VISIBLE
            binding.infoTextView1.visibility = View.GONE
        }

        binding.button5.setOnClickListener {
            binding.infoTextView2.text = "Se necesita Visa"
            binding.infoTextView2.visibility = View.VISIBLE
            binding.infoTextView1.visibility = View.GONE
        }











        // Inicializar Firebase Analytics
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        println("jejejeee")
        println(universityId)
        Log.i("UniversityActivity", universityId ?: "University ID is null")
        println(universityId)

        binding.favorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon(isFavorite)
            viewModel.updateUniversity(LocalUniversity(firebaseId = university?.id, imageUrl = university?.image, favorite = isFavorite))

            // Registra el evento dependiendo de si es favorito o no
            if (isFavorite) {
                Toast.makeText(this, "Universidad agregada a favoritos", Toast.LENGTH_SHORT).show()
                universityFirebaseEvent(university?.name ?: "Unknown University")
                Log.d("analytics","funcionoAgregar")
            } else {
                Toast.makeText(this, "Universidad removida de favoritos", Toast.LENGTH_SHORT).show()
                universityFirebaseEvent(university?.name ?: "Unknown University")
                Log.d("analytics","funcionoEliminar")
            }
        }

        dbref = FirebaseDatabase.getInstance().getReference("Universities")


        val query = dbref.orderByChild("name").equalTo(universityId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val firstUniversitySnapshot = snapshot.children.firstOrNull()
                    university = firstUniversitySnapshot?.getValue(University::class.java)
                    binding.universityName.text = university?.name ?: ""
                    binding.universityLocation.text = university?.country
                    university?.image?.let {
                        Picasso.get().load(it).into(binding.headerImage)
                    }
                    intFavorite(university)

                    binding.chat.setOnClickListener {
                        val intent = Intent(this@UniversityActivity, ChatActivity2::class.java)
                        intent.putExtra("name", university?.name)
                        startActivity(intent)
                    }





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

    private fun intFavorite(university: University?) {
        viewModel.getUniversityById(university?.id)
        viewModel.localSingleUniversity.observe(this){
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

    private fun universityFirebaseEvent(universityName: String?) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, universityName)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, universityName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
        if (universityName != null) {
            firebaseAnalytics.logEvent(universityName, bundle)
        }
    }

}