package com.example.authenticationapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val registerButton = findViewById<Button>(R.id.btn_register)
        registerButton.setOnClickListener {
            val username = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.username).text.toString()
            val email = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.email).text.toString()
            val password = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.password).text.toString()

            when {
                username.isEmpty() -> {
                    findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.username).error = "Username is required"
                }
                email.isEmpty() -> {
                    findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.email).error = "Email is required"
                }
                password.isEmpty() -> {
                    findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.password).error = "Password is required"
                }
                password.length < 6 -> {
                    findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.password).error = "Password must be at least 6 characters"
                }
                else -> {
                    registerUserWithFirebase(email, password)
                }
            }
        }
    }

    private fun registerUserWithFirebase(email: String, password: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    val username = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.username).text.toString()
                    saveUsernameToDatabase(firebaseAuth.currentUser?.uid, username)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUsernameToDatabase(uid: String?, username: String) {
        if (uid != null) {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users").child(uid)
            usersRef.setValue(mapOf("username" to username))
                .addOnSuccessListener {
                    Toast.makeText(this, "User saved successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Failed to save user: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
