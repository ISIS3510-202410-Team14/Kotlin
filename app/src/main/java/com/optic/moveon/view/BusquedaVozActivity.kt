package com.optic.moveon.view

import android.app.Activity
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.optic.moveon.R
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.optic.moveon.databinding.ActivityBusquedaVozBinding
import android.widget.Toast
import android.util.Log
import android.text.Editable
import android.text.TextWatcher
import java.util.Locale

class BusquedaVozActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBusquedaVozBinding
    private lateinit var adaptador: AdaptadorNombres

    var listaNombres = arrayListOf("Harvard", "Barcelona", "Lisboa", "Melbourne", "Paris", "Rome")

    private val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            var result = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.etNombre.setText(result!![0])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBusquedaVozBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvNombres.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorNombres(listaNombres)
        binding.rvNombres.adapter = adaptador

        binding.ibtnMicrofono.setOnClickListener {
            binding.etNombre.setText("")
            escucharVoz()
        }

        binding.etNombre.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

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

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult.launch(intent)
        } else {
            Log.e("ERROR", "Su dispositivo no admite entrada por voz")
            Toast.makeText(applicationContext, "Su dispositivo no admite entrada por voz", Toast.LENGTH_LONG).show()
        }
    }
    fun filtrar(texto: String) {
        val listaFiltrada: ArrayList<String> = arrayListOf()

        for (nombre in listaNombres) {
            if (nombre.toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(nombre)
            }
        }
        adaptador.filtrar(listaFiltrada)
    }
}
