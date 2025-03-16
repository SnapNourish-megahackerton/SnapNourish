package com.cs407.snapnourish

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TransitionActivityforN : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transition_n)

        val capturedImageView = findViewById<ImageView>(R.id.capturedImage)
        val viewNutritionButton = findViewById<Button>(R.id.viewNutritionButton)

        val imageUri = intent.getStringExtra("IMAGE_URI")?.let { Uri.parse(it) }
        capturedImageView.setImageURI(imageUri)

        viewNutritionButton.setOnClickListener {
            val intent = Intent(this, NutritionResultActivity::class.java)
            intent.putExtra("IMAGE_URI", imageUri.toString())
            startActivity(intent)
        }

        // Navigate to Home screen(icon1)
        findViewById<Button>(R.id.btn_home).setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        //Navigate to History screen(icon2)
        findViewById<Button>(R.id.btn_history).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        //Navigate to Chatbot screen(icon3)
        findViewById<Button>(R.id.btn_chat).setOnClickListener {
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }

        // Navigate to Settings screen(icon4)
        findViewById<Button>(R.id.btn_settings).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
