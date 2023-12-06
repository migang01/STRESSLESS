package com.example.stressless

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.stressless.ui.theme.STRESSLESSTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val litmusBtn = findViewById<Button>(R.id.litmusBtn)
        val tempBtn = findViewById<Button>(R.id.tempBtn)
        val bloodoxgBtn = findViewById<Button>(R.id.bloodoxgBtn)
        val dataBtn = findViewById<Button>(R.id.dataBtn)

        litmusBtn.setOnClickListener {
            val intent = Intent(this, LitmusActivity::class.java)
            startActivity(intent)
        }

        tempBtn.setOnClickListener {
            val intent = Intent(this, TempmeasureActivity::class.java)
            startActivity(intent)
        }

        bloodoxgBtn.setOnClickListener {
            val intent = Intent(this, BloodoxygenActivity::class.java)
            startActivity(intent)
        }

        dataBtn.setOnClickListener {
            val intent = Intent(this, DatashowActivity::class.java)
            startActivity(intent)
        }
    }
}