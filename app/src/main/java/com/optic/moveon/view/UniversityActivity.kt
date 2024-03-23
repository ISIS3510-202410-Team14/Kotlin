package com.optic.moveon.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.optic.moveon.R

class UniversityActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var infoTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_university)

        spinner = findViewById(R.id.spinner)
        infoTextView = findViewById(R.id.infoTextView)

        val titles = arrayOf("Agreement", "Faculty", "Academic Program", "Fees", "Content")
        val details = arrayOf(
            "Exchange Student Undergraduate-University of Melbourne-School of Management-Outgoing",
            "Faculty Detail Info...",
            "Academic Program Detail Info...",
            "Fees Detail Info...",
            "Content Detail Info..."
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, titles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                infoTextView.text = details[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                infoTextView.text = ""
            }
        }
    }
}