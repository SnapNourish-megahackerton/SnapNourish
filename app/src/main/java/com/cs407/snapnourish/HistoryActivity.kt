package com.cs407.snapnourish

import ImageAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

class HistoryActivity : AppCompatActivity() {

    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var currentMonthTextView: TextView
    private var calendar = Calendar.getInstance()
    private lateinit var imageAdapter: ImageAdapter
    private val imageUrls = mutableListOf<String>()
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up the views
        photoRecyclerView = findViewById(R.id.photoRecyclerView)
        currentMonthTextView = findViewById(R.id.currentMonth)

        updateMonthDisplay()
        fetchUserImagesForMonth()

        // Set up the RecyclerView
        // Initialize the ImageAdapter with a click listener
        imageAdapter = ImageAdapter(imageUrls) { imageUrl ->
            // This lambda gets triggered when an image is clicked
            val intent = Intent(this, NutritionResultActivity::class.java)
            intent.putExtra("photoUrl", imageUrl)  // Pass the clicked image's URL
            startActivity(intent)
        }
        photoRecyclerView.layoutManager = GridLayoutManager(this, 2)
        photoRecyclerView.adapter = imageAdapter

        val itemDecoration = SpaceItemDecoration(16) // 16dp 간격
        photoRecyclerView.addItemDecoration(itemDecoration)

        // Buttons to change the month
        findViewById<Button>(R.id.btn_previous_month).setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateMonthDisplay()
            fetchUserImagesForMonth()
        }

        findViewById<Button>(R.id.btn_next_month).setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateMonthDisplay()
            fetchUserImagesForMonth()
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

    private fun updateMonthDisplay() {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        currentMonthTextView.text = dateFormat.format(calendar.time)
    }

    private fun fetchUserImagesForMonth() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()

            // Get the selected month (in format "yyyy-MM")
            val monthYear = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(calendar.time)

            // Get the start and end of the current month for filtering
            val startOfMonth = getStartOfMonth(calendar)
            val endOfMonth = getEndOfMonth(calendar)

            // Query Firestore for nutrition info within the selected month range
            db.collection("users").document(userId)
                .collection("nutrition_info")
                .whereGreaterThanOrEqualTo("timestamp", startOfMonth)
                .whereLessThanOrEqualTo("timestamp", endOfMonth)
                .get()
                .addOnSuccessListener { documents ->
                    val urls = documents.mapNotNull { it.getString("photoUrl") }
                    imageUrls.clear()
                    imageUrls.addAll(urls)
                    imageAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching images", exception)
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("HistoryActivity", "User is not logged in")
        }
    }


    // Get the start of the current month (first day of the month)
    private fun getStartOfMonth(calendar: Calendar): Date {
        val startOfMonth = Calendar.getInstance()
        startOfMonth.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1, 0, 0, 0)
        return startOfMonth.time
    }

    // Get the end of the current month (last day of the month)
    private fun getEndOfMonth(calendar: Calendar): Date {
        val endOfMonth = Calendar.getInstance()
        endOfMonth.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        return endOfMonth.time
    }
}
