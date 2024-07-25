package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        val db = Firebase.firestore
        val workoutRef = db.collection(MyFirestoreReferences.USER_WORKOUT_COLLECTION)
        val query = workoutRef.whereEqualTo("email", email)

        if (email != null) {
            query.get().addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val document = documents.first()
                    val benchHint = document.getString(MyFirestoreReferences.DUMBELL_BENCH_PRESS_FIELD) ?: "50"
                    val incHint = document.getString(MyFirestoreReferences.INCLINED_BENCH_PRESS_FIELD) ?: "50"
                    val deadHint = document.getString(MyFirestoreReferences.DEADLIFT_FIELD) ?: "50"
                    val rowHint = document.getString(MyFirestoreReferences.ROWS_FIELD) ?: "50"
                    val squatHint = document.getString(MyFirestoreReferences.SQUATS_FIELD) ?: "50"
                    val jumpHint = document.getString(MyFirestoreReferences.JUMPING_JACKS_FIELD) ?: "50"
                    val bicycleHint = document.getString(MyFirestoreReferences.BICYCLE_CRUNCHES) ?: "50"
                    val burpeeHint = document.getString(MyFirestoreReferences.BURPEES_FIELDS) ?: "50"
                    val pushupHint = document.getString(MyFirestoreReferences.PUSHUPS_FIELD) ?: "50"
                    val kneeHint = document.getString(MyFirestoreReferences.HIGH_KNEES_FIELD) ?: "50"

                    viewBinding.edtDumbbellBenchPress.hint = benchHint
                    viewBinding.edtInclinedBenchPress.hint = incHint
                    viewBinding.edtDeadlift.hint = deadHint
                    viewBinding.edtRows.hint = rowHint
                    viewBinding.edtSquat.hint = squatHint
                    viewBinding.edtJumpingJacks.hint = jumpHint
                    viewBinding.edtBicycleCrunches.hint = bicycleHint
                    viewBinding.edtBurpees.hint = burpeeHint
                    viewBinding.edtPushups.hint = pushupHint
                    viewBinding.edtHighKnees.hint = kneeHint

                } else {
                    Toast.makeText(this, "No user found with this email", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching profile: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Set up ToggleButton listeners
        setupToggleButtons()

        // Fetch initial values from Firestore
        fetchInitialValues(email)

        // Set up save button listener
        viewBinding.btnSaveWorkout.setOnClickListener {

            val updatedDumbellBenchPress = viewBinding.edtDumbbellBenchPress.text.toString()
            val updatedInclineBenchPress = viewBinding.edtInclinedBenchPress.text.toString()
            val updatedDeadlift = viewBinding.edtDeadlift.text.toString()
            val updatedRows = viewBinding.edtRows.text.toString()
            val updatedSquats = viewBinding.edtSquat.text.toString()
            val updatedJumpingJacks = viewBinding.edtJumpingJacks.text.toString()
            val updatedBicycleCrunches = viewBinding.edtBicycleCrunches.text.toString()
            val updatedBurpees = viewBinding.edtBurpees.text.toString()
            val updatedPushups = viewBinding.edtPushups.text.toString()
            val updatedHighKnees = viewBinding.edtHighKnees.text.toString()

            val updatedWorkoutData = mutableMapOf<String, Any>()

            if (updatedDumbellBenchPress.isNotBlank()) {
                updatedWorkoutData[MyFirestoreReferences.DUMBELL_BENCH_PRESS_FIELD] = updatedDumbellBenchPress
            }
            if(updatedInclineBenchPress.isNotBlank()){
                updatedWorkoutData[MyFirestoreReferences.INCLINED_BENCH_PRESS_FIELD] = updatedInclineBenchPress
            }
            if(updatedDeadlift.isNotBlank()){
                updatedWorkoutData[MyFirestoreReferences.DEADLIFT_FIELD] = updatedDeadlift
            }
            if(updatedRows.isNotBlank()){
                updatedWorkoutData[MyFirestoreReferences.ROWS_FIELD] = updatedRows
            }
            if(updatedSquats.isNotBlank()){
                updatedWorkoutData[MyFirestoreReferences.SQUATS_FIELD] = updatedSquats
            }
            if(updatedJumpingJacks.isNotBlank()){
                updatedWorkoutData[MyFirestoreReferences.JUMPING_JACKS_FIELD] = updatedJumpingJacks
            }
            if(updatedBicycleCrunches.isNotBlank()){
                updatedWorkoutData[MyFirestoreReferences.BICYCLE_CRUNCHES] = updatedBicycleCrunches
            }
            if(updatedBurpees.isNotBlank()){
                updatedWorkoutData[MyFirestoreReferences.BURPEES_FIELDS] = updatedBurpees
            }
            if(updatedPushups.isNotBlank()){
                updatedWorkoutData[MyFirestoreReferences.PUSHUPS_FIELD] = updatedPushups
            }
            if(updatedHighKnees.isNotBlank()){
                updatedWorkoutData[MyFirestoreReferences.HIGH_KNEES_FIELD] = updatedHighKnees
            }

            workoutRef.whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val document = documents.first()
                    val documentId = document.id

                    workoutRef.document(documentId).update(updatedWorkoutData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Workout updated successfully", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error updating workout: ${exception.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(this, "No user found with this email", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error finding document: ${exception.message}", Toast.LENGTH_LONG).show()
            }


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
