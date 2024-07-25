package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
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

            val intent = Intent(this, Profile::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
            finish()
        }

    }
}