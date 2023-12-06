package com.example.stressless

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.stressless.ui.theme.STRESSLESSTheme

class DatadetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datadetails)

        /**이전 버튼**/
        val backBtn = findViewById<Button>(R.id.backBtn)
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        /**다시 디테일 액티비티로**/
        val datashowBtn = findViewById<Button>(R.id.datashowBtn)
        datashowBtn.setOnClickListener {
            val intent = Intent(this, DatashowActivity::class.java)
            startActivity(intent)
        }
    }
}