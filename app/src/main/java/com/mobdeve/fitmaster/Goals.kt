package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobdeve.fitmaster.databinding.ActivityGoalsBinding

class Goals : AppCompatActivity() {

    private lateinit var viewBinding: ActivityGoalsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val email = this.intent.getStringExtra("email")
        val db = Firebase.firestore
        val usersRef = db.collection("Users")
        val query = usersRef.whereEqualTo("email", email)

        query.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val level = document.getString(MyFirestoreReferences.LEVEL_FIELD)
                when (level) {
                    "beginner" -> viewBinding.btnBeginner.isChecked = true
                    "intermediate" -> viewBinding.btnIntermediate.isChecked = true
                    "advanced" -> viewBinding.btnAdvanced.isChecked = true
                }
            }
        }

        query.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val goal = document.getString(MyFirestoreReferences.GOAL_FIELD)
                when (goal) {
                    "loseWeight" -> viewBinding.btnWeightLoss.isChecked = true
                    "gainMuscle" -> viewBinding.btnGainMuscles.isChecked = true
                }
            }
        }

        query.get().addOnSuccessListener { documents ->
            val goalWeight = documents.first().getString(MyFirestoreReferences.GOAL_WEIGHT_FIELD)
            viewBinding.etTargetWeight.hint = "$goalWeight kg" ?: "Enter target weight in kg"
        }

        viewBinding.btnBeginner.setOnClickListener {
            if (viewBinding.btnBeginner.isChecked) {
                viewBinding.btnIntermediate.isChecked = false
                viewBinding.btnAdvanced.isChecked = false
            }
        }

        viewBinding.btnIntermediate.setOnClickListener {
            if (viewBinding.btnIntermediate.isChecked) {
                viewBinding.btnBeginner.isChecked = false
                viewBinding.btnAdvanced.isChecked = false
            }
        }

        viewBinding.btnAdvanced.setOnClickListener {
            if (viewBinding.btnAdvanced.isChecked) {
                viewBinding.btnBeginner.isChecked = false
                viewBinding.btnIntermediate.isChecked = false
            }
        }

        viewBinding.btnWeightLoss.setOnClickListener {
            if (viewBinding.btnWeightLoss.isChecked) {
                viewBinding.btnGainMuscles.isChecked = false
            }
        }

        viewBinding.btnGainMuscles.setOnClickListener {
            if (viewBinding.btnGainMuscles.isChecked) {
                viewBinding.btnWeightLoss.isChecked = false
            }
        }

        viewBinding.btnSave.setOnClickListener {

            val newLevel = when {
                viewBinding.btnBeginner.isChecked -> "beginner"
                viewBinding.btnIntermediate.isChecked -> "intermediate"
                viewBinding.btnAdvanced.isChecked -> "advanced"
                else -> "beginner"
            }

            val newGoal = when {
                viewBinding.btnWeightLoss.isChecked -> "loseWeight"
                viewBinding.btnGainMuscles.isChecked -> "gainMuscle"
                else -> "loseWeight"
            }
            val updatedHeight = viewBinding.etEnterHeight.text.toString()
            val updatedWeight = viewBinding.etEnterWeight.text.toString()

            // Update height and weight
            val targetWeight = viewBinding.etTargetWeight.text.toString()
            val updatedData = mutableMapOf<String, Any>()
            if (updatedWeight.isNotBlank()) {
                updatedData[MyFirestoreReferences.WEIGHT_FIELD] = updatedWeight
            }
            if (updatedHeight.isNotBlank()) {
                updatedData[MyFirestoreReferences.HEIGHT_FIELD] = updatedHeight
            }

            usersRef.whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val document = documents.first()
                    val documentId = document.id

                    usersRef.document(documentId).update(updatedData)
                } else {
                    Toast.makeText(this, "No user found with this email", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error finding document: ${exception.message}", Toast.LENGTH_LONG).show()
            }

            // Update Goals
            query.get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    usersRef.document(userId).update(MyFirestoreReferences.LEVEL_FIELD, newLevel)
                    usersRef.document(userId).update(MyFirestoreReferences.GOAL_FIELD, newGoal)
                    if(targetWeight.isNotBlank()){
                        usersRef.document(userId).update(MyFirestoreReferences.GOAL_WEIGHT_FIELD, targetWeight)
                    }
                }

                val intent = Intent(this, Profile::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
                finish()
            }
        }
    }
}