package com.example.lanedetectionopencv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val laneDetectionButton = findViewById<Button>(R.id.laneDetectionButton)
        val fatigueDetectionButton = findViewById<Button>(R.id.fatigueDetectionButton)

        laneDetectionButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        fatigueDetectionButton.setOnClickListener {
            val intent = Intent(this, FatigueDetectionActivity::class.java)
            startActivity(intent)
        }
    }
}
