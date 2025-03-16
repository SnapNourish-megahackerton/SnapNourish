package com.cs407.snapnourish

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecipeResultActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_result)

        val resultImageView = findViewById<ImageView>(R.id.resultImageView)
        val recipeTitleTextView = findViewById<TextView>(R.id.RecipeTitleTextView)
        val recipeStepsTextView = findViewById<TextView>(R.id.RecipeStepsTextView)
        val recipeVideoLinkTextView = findViewById<TextView>(R.id.RecipeVideoLinkTextView)
        val nutritionInfoTextView = findViewById<TextView>(R.id.nutritionInfoTextView)

        val recipeDocumentId = "WpJovpMt5U2tFiBgGLbb"


        db.collection("recipe_info").document(recipeDocumentId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Load image
                    val photoUrl = document.getString("photoUrl")
                    photoUrl?.let { gsUrl ->
                        loadImageFromStorage(gsUrl, resultImageView)
                    }

                    // Set recipe title
                    val recipeTitle = document.getString("recipeTitle") ?: "No Recipe Title"
                    recipeTitleTextView.text = recipeTitle

                    // Set recipe steps
                    val recipeSteps = document.getString("recipeSteps") ?: "No steps provided."
                    recipeStepsTextView.text = recipeSteps

                    // Set recipe video link
                    val recipeVideoUrl = document.getString("recipeVideoUrl")
                    if (recipeVideoUrl != null) {
                        recipeVideoLinkTextView.text = "Watch Recipe Video"
                        recipeVideoLinkTextView.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recipeVideoUrl))
                            startActivity(intent)
                        }
                    } else {
                        recipeVideoLinkTextView.text = "No video available."
                    }

                    // Set nutrition info
                    val calories = document.getDouble("calories") ?: 0.0
                    val carbohydrates = document.getDouble("carbohydrates") ?: 0.0
                    val protein = document.getDouble("protein") ?: 0.0
                    val fiber = document.getDouble("fiber") ?: 0.0
                    val saturatedFat = document.getDouble("saturatedFat") ?: 0.0
                    val unsaturatedFat = document.getDouble("unsaturatedFat") ?: 0.0

                    val nutritionInfo = """
                Calories: $calories
                Carbohydrates: $carbohydrates g
                Protein: $protein g
                Fiber: $fiber g
                Saturated Fat: $saturatedFat g
                Unsaturated Fat: $unsaturatedFat g
            """.trimIndent()
                    nutritionInfoTextView.text = nutritionInfo
                } else {
                    Toast.makeText(this, "Document not found!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load recipe information.", Toast.LENGTH_SHORT).show()
            }



//        // Firestore
//        db.collection("recipe_photos").get()
//            .addOnSuccessListener { documents ->
//                for (document in documents) {
//                    val photoUrl = document.getString("photoUrl")
//                    val recipe = document.getString("recipe") ?: "No recipe information available."
//                    val timestamp = document.getLong("timestamp") ?: System.currentTimeMillis()
//
//                    if (photoUrl != null) {
//                        displayPhoto(photoUrl, recipe, timestamp, resultImageView, recipeTitleTextView, nutritionInfoTextView)
//                    }
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Failed to load recipe photos", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun displayPhoto(
//        photoUrl: String,
//        recipe: String,
//        timestamp: Long,
//        resultImageView: ImageView,
//        recipeTitleTextView: TextView,
//        nutritionInfoTextView: TextView
//    ) {
//        // image
//        Glide.with(this).load(Uri.parse(photoUrl)).into(resultImageView)
//
//        // Recipe title
//        recipeTitleTextView.text = "Recipe"
//
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
//        val formattedTimestamp = dateFormat.format(Date(timestamp))
//
//        // Recipe
//        nutritionInfoTextView.text = """
//            $recipe
//        """.trimIndent()
//


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

    private fun loadImageFromStorage(gsUrl: String, imageView: ImageView) {
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(gsUrl)
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this).load(uri).into(imageView)
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Failed to load image.", Toast.LENGTH_SHORT).show()
        }
    }

}
