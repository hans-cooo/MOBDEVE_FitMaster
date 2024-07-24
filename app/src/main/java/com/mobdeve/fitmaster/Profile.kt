package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobdeve.fitmaster.databinding.ProfileBinding

class Profile : ComponentActivity() {
    private lateinit var viewBinding: ProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = ProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        auth = FirebaseAuth.getInstance()
        val email = this.intent.getStringExtra("email")

        val db = Firebase.firestore
        val usersRef = db.collection("Users")
        val query = usersRef.whereEqualTo("email", email)

        // Set hints
        if (email != null) {
            query.get().addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val document = documents.first()
                    val birthday = document.getString("birthday") ?: ""
                    val weight = document.getString("weight") ?: "Weight in"
                    val height = document.getString("height") ?: "Height in"

                    viewBinding.edtProfileBDay.hint = birthday
                    viewBinding.edtProfileWeight.hint = "$weight kg"
                    viewBinding.edtProfileHeight.hint = "$height cm"
                } else {
                    Toast.makeText(this, "No user found with this email", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching profile: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        viewBinding.btnSaveChanges.setOnClickListener {
            // Get the updated data from EditText fields
            val updatedBirthday = viewBinding.edtProfileBDay.text.toString()
            val updatedWeight = viewBinding.edtProfileWeight.text.toString()
            val updatedHeight = viewBinding.edtProfileHeight.text.toString()

            // Create a map with the updated data, only including non-empty fields
            val updatedData = mutableMapOf<String, Any>()

            if (updatedBirthday.isNotBlank()) {
                updatedData["birthday"] = updatedBirthday
            }
            if (updatedWeight.isNotBlank()) {
                updatedData["weight"] = updatedWeight
            }
            if (updatedHeight.isNotBlank()) {
                updatedData["height"] = updatedHeight
            }

            // Query for the document where email matches and update it
            usersRef.whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val document = documents.first()
                    val documentId = document.id

                    usersRef.document(documentId).update(updatedData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, Dashboard::class.java)
                            intent.putExtra("email", email)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error updating profile: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "No user found with this email", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error finding document: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Log Out Button
        viewBinding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // Change Password Button
        viewBinding.tvwChangePassword.setOnClickListener {
            if (auth.currentUser != null) {
                auth.sendPasswordResetEmail(auth.currentUser!!.email!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Unable to send reset mail", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
