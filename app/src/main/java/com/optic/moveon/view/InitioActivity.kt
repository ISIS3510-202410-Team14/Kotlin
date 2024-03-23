package com.optic.moveon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.optic.moveon.R


class InitioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        val btn1= findViewById<AppCompatButton>(R.id.btn1)

        btn1.setOnClickListener {
                val intent= Intent(this,AuthActivity::class.java)
                startActivity(intent)
        }


    }
}