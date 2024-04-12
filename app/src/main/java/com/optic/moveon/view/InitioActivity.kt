package com.optic.moveon.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import com.optic.moveon.R
import com.optic.moveon.model.UserSessionManager


class InitioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)
        val btn1= findViewById<AppCompatButton>(R.id.btn1)

        UserSessionManager.init(this.applicationContext)
        if (UserSessionManager.isUserLoggedIn()){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        btn1.setOnClickListener {
                val intent= Intent(this,AuthActivity::class.java)
                startActivity(intent)
        }




    }
}