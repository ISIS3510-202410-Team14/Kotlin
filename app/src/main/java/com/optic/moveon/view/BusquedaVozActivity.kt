package com.optic.moveon.view

import android.content.Context
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.ConnectivityManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.optic.moveon.databinding.ActivityBusquedaVozBinding
import android.widget.Toast
import android.util.Log
import android.text.Editable
import android.text.TextWatcher

class BusquedaVozActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBusquedaVozBinding
    private lateinit var adaptador: AdaptadorNombres

    var listaNombres = arrayListOf("Harvard", "Barcelona", "Lisboa", "Melbourne", "Paris",
        "Rome", "Oporto", "Buenos Aires", "Chile", "Sao Paulo", "Panamá",
        "República Dominicana", "Autónoma", "Politécnico", "Monterrey", "Valencia", "Madrid",
        "Nantes", "Grenoble", "Sttutgart", "AGH Polonia")
    var listaUrls = arrayListOf(
        "https://www.harvard.edu/",
        "https://www.ub.edu/",
        "https://www.ulisboa.pt/",
        "https://www.unimelb.edu.au/",
        "https://www.psbedu.paris",
        "https://www.unica.it/",
        "https://www.up.pt/portal/pt/",
        "https://www.itba.edu.ar",
        "https://www.uc.cl",
        "https://www5.usp.br",
        "https://utp.ac.pa",
        "https://pucmm.edu.do",
        "https://www.unam.mx",
        "https://www.ipn.mx",
        "https://www.tec.mx",
        "https://www.upv.es",
        "https://www.upm.es",
        "https://www.ec-nantes.fr",
        "https://www.grenoble-inp.fr/en",
        "https://www.uni-stuttgart.de/en/",
        "https://www.agh.edu.pl/en/"
    )

    private val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            var result = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.etNombre.setText(result!![0])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusquedaVozBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvNombres.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorNombres(listaNombres, listaUrls)
        binding.rvNombres.adapter = adaptador

        binding.ibtnMicrofono.setOnClickListener {
            binding.etNombre.setText("")
            escucharVoz()
        }

        binding.etNombre.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                filtrar(p0.toString())
            }

        })
    }

    fun escucharVoz() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US, es")

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult.launch(intent)
        } else {
            Log.e("ERROR", "Su dispositivo no admite entrada por voz")
            Toast.makeText(applicationContext, "Su dispositivo no admite entrada por voz", Toast.LENGTH_LONG).show()
        }
    }

    fun filtrar(texto: String) {
        adaptador.filtrar(texto)
    }

    @Suppress("DEPRECATION")
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
