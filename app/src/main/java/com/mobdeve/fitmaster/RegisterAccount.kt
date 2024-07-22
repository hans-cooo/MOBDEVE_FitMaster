package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.mobdeve.fitmaster.databinding.RegisterAccountBinding

class RegisterAccount : AppCompatActivity() {
    private lateinit var viewBinding: RegisterAccountBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = RegisterAccountBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        auth = FirebaseAuth.getInstance()

        viewBinding.tvwLoginRedirect.setOnClickListener() {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        // Create an Account
        viewBinding.btnCreate.setOnClickListener() {
            val name = viewBinding.edtFullName.text.toString()
            val birthday = viewBinding.edtBirthday.text.toString()
            val email = viewBinding.edtEmail.text.toString()
            val password = viewBinding.edtPassword.text.toString()

            val db = Firebase.firestore

            val usersRef = db.collection(MyFirestoreReferences.USERS_COLLECTION)


            if(name.isEmpty() or birthday.isEmpty() or email.isEmpty() or password.isEmpty()){
                Toast.makeText(this, "Fill Incomplete Fields", Toast.LENGTH_LONG).show()
            }
            else if(password.length < 6){
                Toast.makeText(this, "Password should be at least 6 characters.", Toast.LENGTH_LONG).show()
            }
            else{
                val data = hashMapOf<String, Any>(
                    MyFirestoreReferences.USERNAME_FIELD to name,
                    MyFirestoreReferences.BIRTHDAY_FIELD to birthday,
                    MyFirestoreReferences.EMAIL_FIELD to email,
                    MyFirestoreReferences.PASSWORD_FIELD to password
                )
                usersRef.add(data)
                    .addOnSuccessListener { documentReference ->
                        // For successful upload
                        Toast.makeText(this, "User added with ID: ${documentReference.id}", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { e ->
                        // For failed upload
                        Toast.makeText(this, "Error adding user: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }



    }
}