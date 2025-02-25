package com.cs407.snapnourish

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Handler
import android.os.Looper


class NutritionResultActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutrition_result)

        val resultImageView = findViewById<ImageView>(R.id.resultImageView)
        val foodNameTextView = findViewById<TextView>(R.id.foodNameTextView)
        val timestampTextView = findViewById<TextView>(R.id.timestampTextView)
        val nutritionInfoTextView = findViewById<TextView>(R.id.nutritionInfoTextView)
        val missingNutrientsTextView = findViewById<TextView>(R.id.missingNutrientsTextView)
        val recommendIngredientsTextView = findViewById<TextView>(R.id.recommendIngredientsTextView)
        val carbonFootprintTextView = findViewById<TextView>(R.id.carbonFootprintTextView)

        fetchLatestNutritionData(
            resultImageView, foodNameTextView, timestampTextView,
            nutritionInfoTextView, missingNutrientsTextView, recommendIngredientsTextView, carbonFootprintTextView
        )

        // Navigation buttons
        findViewById<Button>(R.id.btn_home).setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }
        findViewById<Button>(R.id.btn_history).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        findViewById<Button>(R.id.btn_chat).setOnClickListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        }
        findViewById<Button>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun fetchLatestNutritionData(
        resultImageView: ImageView,
        foodNameTextView: TextView,
        timestampTextView: TextView,
        nutritionInfoTextView: TextView,
        missingNutrientsTextView: TextView,
        recommendIngredientsTextView: TextView,
        carbonFootprintTextView: TextView
    ) {
        val user = auth.currentUser
        if (user == null) {
            Log.e("NutritionResultActivity", "User is not logged in.")
            return
        }

        val nutritionRef = db.collection("users").document(user.uid).collection("nutrition_info")

        // UI를 잠시 로딩 상태로 변경
        foodNameTextView.text = "Loading latest data..."
        timestampTextView.text = ""
        nutritionInfoTextView.text = ""
        missingNutrientsTextView.text = ""
        recommendIngredientsTextView.text = ""
        carbonFootprintTextView.text = ""

        nutritionRef.orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("Firestore", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    val latestDoc = snapshots.documents[0]
                    val timestamp = latestDoc.getTimestamp("timestamp")?.toDate()

                    // 🛑 최신 데이터인지 확인 (현재 시간과 비교)
                    if (timestamp != null) {
                        val currentTime = System.currentTimeMillis()
                        val dataTime = timestamp.time

                        // 데이터가 너무 오래된 경우 (1초 이상 차이 나면 무시)
                        if (currentTime - dataTime > 8000) {
                            Log.w("Firestore", "Skipping outdated data.")
                            return@addSnapshotListener
                        }
                    }

                    val photoUrl = latestDoc.getString("photoUrl") ?: ""
                    val ingredientsList = latestDoc.get("ingredients") as? List<Map<String, Any>> ?: emptyList()
                    val nutrientDeficiencies = latestDoc.get("nutrient_deficiencies") as? List<Map<String, Any>> ?: emptyList()
                    val recommendations = latestDoc.get("recommendations") as? List<Map<String, Any>> ?: emptyList()

                    // UI 업데이트
                    Glide.with(this).load(photoUrl).into(resultImageView)

                    val foodName = ingredientsList.firstOrNull()?.get("name") as? String ?: "Unknown Food"
                    foodNameTextView.text = "Food: $foodName"

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    timestampTextView.text = "Captured on: ${timestamp?.let { dateFormat.format(it) } ?: "Unknown"}"

                    // 영양 정보 (carbon footprint 제외)
                    val nutritionText = ingredientsList.joinToString("\n") { ingredient ->
                        val name = ingredient["name"] as? String ?: "Unknown"
                        val values = ingredient.filterKeys { it != "carbon_footprint" && it != "name" }

                        val details = values.entries.joinToString(", ") { (key, value) ->
                            "$key: $value"
                        }

                        "$name → $details"
                    }
                    nutritionInfoTextView.text = nutritionText

                    // Carbon footprint
                    val carbonFootprintValues = ingredientsList.mapNotNull { it["carbon_footprint"] as? String }
                    val carbonFootprintText = if (carbonFootprintValues.isNotEmpty()) {
                        "Carbon Footprint: " + carbonFootprintValues.joinToString(", ")
                    } else {
                        "Carbon Footprint: No data available"
                    }
                    carbonFootprintTextView.text = carbonFootprintText

                    // 부족한 영양소
                    val nutrientText = nutrientDeficiencies.joinToString("\n") { deficiency ->
                        val nutrient = deficiency["nutrient"] as? String ?: "Unknown"
                        val amount = deficiency["deficiency_amount"] as? String ?: "N/A"
                        "$nutrient: $amount"
                    }
                    missingNutrientsTextView.text = nutrientText

                    // 추천 음식
                    val recommendText = recommendations.joinToString("\n") { rec ->
                        val name = rec["name"] as? String ?: "Unknown"
                        val description = rec["description"] as? String ?: "N/A"
                        "$name: $description"
                    }
                    recommendIngredientsTextView.text = recommendText
                } else {
                    Log.e("NutritionResultActivity", "No nutrition data found.")
                }
            }
    }


}
