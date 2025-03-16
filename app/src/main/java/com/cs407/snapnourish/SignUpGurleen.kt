package com.cs407.snapnourish

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignUpGurleen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signupgurleen)

        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup)) { v, insets ->
        //    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        //    insets
        //}

        //Move to Home.kt Page
        //findViewById<Button>(R.id.doneButton).setOnClickListener {
        //    val intent = Intent(this, Home::class.java)
        //    startActivity(intent)
        //}
    }
    var auth = FirebaseAuth.getInstance()
    fun register(view: View) {
        val userEmail = findViewById<EditText>(R.id.emailInput)
        val email = userEmail.text.toString()
        val userPassword = findViewById<EditText>(R.id.passwordInput)
        val password = userPassword.text.toString()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val intent = Intent(this,Home::class.java)
                startActivity(intent)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_LONG).show()
        }
    }
}