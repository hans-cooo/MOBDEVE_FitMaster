package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
            val userProgressRef = db.collection(MyFirestoreReferences.USER_PROGRESS_COLLECTION)
            val userWorkoutRef = db.collection(MyFirestoreReferences.USER_WORKOUT_COLLECTION)

            if(name.isEmpty() or birthday.isEmpty() or email.isEmpty() or password.isEmpty()){
                Toast.makeText(this, "Fill Incomplete Fields", Toast.LENGTH_LONG).show()
            }
            else if(password.length < 6){
                Toast.makeText(this, "Password should be at least 6 characters.", Toast.LENGTH_LONG).show()
            }
            else{
                val data = hashMapOf(
                    MyFirestoreReferences.USERNAME_FIELD to name,
                    MyFirestoreReferences.BIRTHDAY_FIELD to birthday,
                    MyFirestoreReferences.EMAIL_FIELD to email,
                    MyFirestoreReferences.PASSWORD_FIELD to password
                )
                usersRef.add(data)
                    .addOnSuccessListener { documentReference ->
                        // For successful upload
                        Log.d("RegisterAccount", "User added with ID: ${documentReference.id}")
                        Toast.makeText(this, "User added with ID: ${documentReference.id}", Toast.LENGTH_LONG).show()

                        val defaultWorkoutData = hashMapOf(
                            MyFirestoreReferences.EMAIL_FIELD to email,
                            MyFirestoreReferences.MONDAY_FIELD to false,
                            MyFirestoreReferences.TUESDAY_FIELD to false,
                            MyFirestoreReferences.WEDNESDAY_FIELD to false,
                            MyFirestoreReferences.THURSDAY_FIELD to false,
                            MyFirestoreReferences.FRIDAY_FIELD to false,
                            MyFirestoreReferences.SATURDAY_FIELD to false,
                            MyFirestoreReferences.SUNDAY_FIELD to false,

                            MyFirestoreReferences.BICYCLE_CRUNCHES to "25",
                            MyFirestoreReferences.BURPEES_FIELDS to "20",
                            MyFirestoreReferences.DEADLIFT_FIELD to "45",
                            MyFirestoreReferences.DUMBELL_BENCH_PRESS_FIELD to "25",
                            MyFirestoreReferences.HIGH_KNEES_FIELD to "15",
                            MyFirestoreReferences.INCLINED_BENCH_PRESS_FIELD to "10",
                            MyFirestoreReferences.PUSHUPS_FIELD to "15",
                            MyFirestoreReferences.SQUATS_FIELD to "30",
                            MyFirestoreReferences.JUMPING_JACKS_FIELD to "20",
                            MyFirestoreReferences.ROWS_FIELD to "12",
                            MyFirestoreReferences.JOG_MINUTES to "10"

                        )

                        userWorkoutRef.add(defaultWorkoutData)


                        val defaultProgressData = hashMapOf(
                            MyFirestoreReferences.EMAIL_FIELD to email,
                            MyFirestoreReferences.DAY_1_FIELD to "empty",
                            MyFirestoreReferences.DAY_2_FIELD to "empty",
                            MyFirestoreReferences.DAY_3_FIELD to "empty",
                            MyFirestoreReferences.DAY_4_FIELD to "empty",
                            MyFirestoreReferences.DAY_5_FIELD to "empty",
                            MyFirestoreReferences.DAY_6_FIELD to "empty",
                            MyFirestoreReferences.DAY_7_FIELD to "empty"
                        )
                        userProgressRef.add(defaultProgressData).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(this, Dashboard::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("email", email)
                                startActivity(intent)
                                finish()
                            } else {
                                // Handle the error
                                Log.e("Firestore", "Error adding document", task.exception)
                                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        // For failed upload
                        Toast.makeText(this, "Error adding user: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }



    }
}