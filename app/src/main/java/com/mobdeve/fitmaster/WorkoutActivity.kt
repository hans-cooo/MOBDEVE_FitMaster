package com.mobdeve.fitmaster

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mobdeve.fitmaster.databinding.ActivityWorkoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WorkoutActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWorkoutBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val email = this.intent.getStringExtra("email")
        val db = Firebase.firestore
        val usersRef = db.collection(MyFirestoreReferences.USERS_COLLECTION)
        val workoutRef = db.collection(MyFirestoreReferences.USER_WORKOUT_COLLECTION)
        val query = usersRef.whereEqualTo(MyFirestoreReferences.EMAIL_FIELD, email)
        val workoutQuery = workoutRef.whereEqualTo(MyFirestoreReferences.EMAIL_FIELD, email)

        var reps: String? = null

        // Sets workout routine based on user's level, goal, and set weights
        query.get().addOnSuccessListener { documents ->
            val document = documents.first()
            val level = document.getString(MyFirestoreReferences.LEVEL_FIELD)
            val goal = document.getString(MyFirestoreReferences.GOAL_FIELD)

            reps = when (level) {
                "beginner" -> "6"
                "intermediate" -> "8"
                "advanced" -> "10"
                else -> "1,000,000"
            }

            workoutQuery.get().addOnSuccessListener { documents2 ->
                val document2 = documents2.first()
                val benchPress = document2.getString(MyFirestoreReferences.DUMBELL_BENCH_PRESS_FIELD)
                val inclineBenchPress = document2.getString(MyFirestoreReferences.INCLINED_BENCH_PRESS_FIELD)
                val squat = document2.getString(MyFirestoreReferences.SQUATS_FIELD)
                val row = document2.getString(MyFirestoreReferences.ROWS_FIELD)
                val deadlift = document2.getString(MyFirestoreReferences.DEADLIFT_FIELD)
                val bicycleCrunches = document2.getString(MyFirestoreReferences.BICYCLE_CRUNCHES)
                val burpees = document2.getString(MyFirestoreReferences.BURPEES_FIELD)
                val jumpingJacks = document2.getString(MyFirestoreReferences.JUMPING_JACKS_FIELD)
                val highKnees = document2.getString(MyFirestoreReferences.HIGH_KNEES_FIELD)
                val pushups = document2.getString(MyFirestoreReferences.PUSHUPS_FIELD)
                val jogMinutes = document2.getString(MyFirestoreReferences.JOG_MINUTES)

                if(goal == "loseWeight"){
                    loseWeightRoutine(bicycleCrunches.toString(), burpees.toString(), jumpingJacks.toString(), highKnees.toString(), pushups.toString())
                }else if(goal == "gainMuscle"){
                    gainMuscleRoutine(reps.toString(), benchPress.toString(), inclineBenchPress.toString(), squat.toString(), row.toString(), deadlift.toString())
                }

            }
        }



        // Set up Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        this.recyclerView = viewBinding.exerciseRecycler
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun loseWeightRoutine(bicycleCrunches: String, burpees: String, jumpingJacks: String, highKnees: String, pushups: String){
        CoroutineScope(Dispatchers.Main).launch {
            val exerciseList: ArrayList<ExcerciseData> = DataGenerator.generateLoseWeightExercises(bicycleCrunches, burpees, jumpingJacks, highKnees, pushups)
            recyclerView.adapter = ExerciseAdapter(exerciseList)
        }
    }

    private fun gainMuscleRoutine(reps: String, benchPress: String, inclineBenchPress: String, squat: String, row: String, deadlift: String){
        CoroutineScope(Dispatchers.Main).launch {
            // Now you can use the passed-in variables within the coroutine
            val exerciseList: ArrayList<ExcerciseData> = DataGenerator.generateGainMuscleExercises(reps, benchPress, inclineBenchPress, squat, row, deadlift)
            recyclerView.adapter = ExerciseAdapter(exerciseList)
        }
    }
}
