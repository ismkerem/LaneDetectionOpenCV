package com.example.lanedetectionopencv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val laneDetectionButton = findViewById<Button>(R.id.laneDetectionButton)
        laneDetectionButton.setOnClickListener {
            try {
                val intent = Intent(this, LaneDetectionActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("MainActivity", "LaneDetectionActivity başlatılamadı: ${e.message}")
                Toast.makeText(this, "Şerit takip başlatılamadı: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        val fatigueDetectionButton = findViewById<Button>(R.id.fatigueDetectionButton)
        fatigueDetectionButton.setOnClickListener {
            val intent = Intent(this, FatigueDetectionActivity::class.java)
            startActivity(intent)
        }

        val speedCorridorButton = findViewById<Button>(R.id.button)
        speedCorridorButton.setOnClickListener {
            val intent = Intent(this, DetectionActivity::class.java)
            startActivity(intent)
        }

        val settingsButton = findViewById<ImageButton>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}