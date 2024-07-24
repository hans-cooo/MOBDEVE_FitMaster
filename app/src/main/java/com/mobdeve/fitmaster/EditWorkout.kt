package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobdeve.fitmaster.databinding.EditWorkoutBinding

class EditWorkout : AppCompatActivity() {
    private lateinit var viewBinding: EditWorkoutBinding
    private val toggleStates = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = EditWorkoutBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(viewBinding.root)

        val email = this.intent.getStringExtra("email") ?: return


        // Set up ToggleButton listeners
        setupToggleButtons()

        // Fetch initial values from Firestore
        fetchInitialValues(email)

        // Set up save button listener
        viewBinding.btnSaveWorkout.setOnClickListener {
            saveWorkoutChanges(email)
        }
    }

    private fun setupToggleButtons() {
        viewBinding.btnSun.setOnCheckedChangeListener { _, isChecked ->
            toggleStates["daySun"] = isChecked
        }
        viewBinding.btnMon.setOnCheckedChangeListener { _, isChecked ->
            toggleStates["dayMon"] = isChecked
        }
        viewBinding.btnTue.setOnCheckedChangeListener { _, isChecked ->
            toggleStates["dayTue"] = isChecked
        }
        viewBinding.btnWed.setOnCheckedChangeListener { _, isChecked ->
            toggleStates["dayWed"] = isChecked
        }
        viewBinding.btnThu.setOnCheckedChangeListener { _, isChecked ->
            toggleStates["dayThu"] = isChecked
        }
        viewBinding.btnFri.setOnCheckedChangeListener { _, isChecked ->
            toggleStates["dayFri"] = isChecked
        }
        viewBinding.btnSat.setOnCheckedChangeListener { _, isChecked ->
            toggleStates["daySat"] = isChecked
        }
    }

    private fun fetchInitialValues(email: String) {
        val db = Firebase.firestore
        val workoutRef = db.collection(MyFirestoreReferences.USER_WORKOUT_COLLECTION)
        val query = workoutRef.whereEqualTo("email", email)

        query.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val data = document.data
                viewBinding.btnSun.isChecked = data["daySun"] as? Boolean ?: false
                viewBinding.btnMon.isChecked = data["dayMon"] as? Boolean ?: false
                viewBinding.btnTue.isChecked = data["dayTue"] as? Boolean ?: false
                viewBinding.btnWed.isChecked = data["dayWed"] as? Boolean ?: false
                viewBinding.btnThu.isChecked = data["dayThu"] as? Boolean ?: false
                viewBinding.btnFri.isChecked = data["dayFri"] as? Boolean ?: false
                viewBinding.btnSat.isChecked = data["daySat"] as? Boolean ?: false

                // Initialize toggleStates with the fetched values
                toggleStates["daySun"] = viewBinding.btnSun.isChecked
                toggleStates["dayMon"] = viewBinding.btnMon.isChecked
                toggleStates["dayTue"] = viewBinding.btnTue.isChecked
                toggleStates["dayWed"] = viewBinding.btnWed.isChecked
                toggleStates["dayThu"] = viewBinding.btnThu.isChecked
                toggleStates["dayFri"] = viewBinding.btnFri.isChecked
                toggleStates["daySat"] = viewBinding.btnSat.isChecked
            }
        }.addOnFailureListener { exception ->
            Log.w("Firestore", "Error getting documents: ", exception)
        }
    }

    private fun saveWorkoutChanges(email: String) {
        val db = Firebase.firestore
        val workoutRef = db.collection(MyFirestoreReferences.USER_WORKOUT_COLLECTION)
        val query = workoutRef.whereEqualTo("email", email)

        query.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val docRef = workoutRef.document(document.id)
                docRef.update(toggleStates as Map<String, Any>)
                    .addOnSuccessListener { Log.d("Firestore", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("Firestore", "Error updating document", e) }
            }
        }.addOnFailureListener { exception ->
            Log.w("Firestore", "Error getting documents: ", exception)
        }

        val intent = Intent(this, Dashboard::class.java)
        intent.putExtra("email", email)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
