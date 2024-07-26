package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
    private lateinit var countDownTimer: CountDownTimer
    private var isTimerRunning = false
    private var timeLeftInMillis = 900000L
    private var isWorkoutStarted = false
    private var startTime = 0L
    private val exercises = ArrayList<ExerciseData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val email = this.intent.getStringExtra("email")
        val day = this.intent.getStringExtra("day") // The day that was pressed

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

                // Chooses exercise list based on goals
                val exerciseList = when(goal){
                    "loseWeight" -> MyFirestoreReferences.EXERCISE_LIST_LOSE_WEIGHT
                    "gainMuscle" -> MyFirestoreReferences.EXERCISE_LIST_GAIN_MUSCLE
                    else -> MyFirestoreReferences.EXERCISE_LIST
                }
                // Creates data for recyclerview
                for (exerciseName in exerciseList){
                    val value = document2.getString(exerciseName).toString()
                    val name = MyFirestoreReferences.getName(exerciseName)
                    if(goal == "loseWeight"){
                        exercises.add(ExerciseData(name, "", value)) }
                    else {
                        exercises.add(ExerciseData(name, value, reps.toString())) }
                }

                viewBinding.exerciseRecycler.adapter = ExerciseAdapter(exercises)


                /*
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
                */
            }
        }

        viewBinding.btnFinWorkout.setOnClickListener(){
            // TODO: Make this go to summary and complete any other logic relating to the workout ending
            // remember to finish() after the intent and finish() when going from summary to dashboard
            if (isWorkoutStarted) {
                finishWorkout()
            }
        }

        viewBinding.btnStartWorkout.setOnClickListener {
            if (!isWorkoutStarted) {
                startWorkoutTimer()
                isWorkoutStarted = true
                viewBinding.btnStartWorkout.isEnabled = false
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

        // Timer
        viewBinding.btnStartTimer.setOnClickListener {
            if (!isTimerRunning) {
                startTimer()
                viewBinding.btnStartTimer.text = "Pause"
            } else {
                pauseTimer()
                viewBinding.btnStartTimer.text = "Start"
            }
        }

    }

    private fun startWorkoutTimer() {
        // Capture the start time in milliseconds
        startTime = System.currentTimeMillis()
    }

    private fun finishWorkout() {
        // Capture the end time in milliseconds
        val endTime = System.currentTimeMillis()
        // Calculate the duration in milliseconds
        val duration = endTime - startTime

        // Pass the duration to the Summary activity
        val intent = Intent(this, Summary::class.java)
        intent.putExtra("WORKOUT_DURATION", duration)
        startActivity(intent)
        finish()
    }

    private fun loseWeightRoutine(bicycleCrunches: String, burpees: String, jumpingJacks: String, highKnees: String, pushups: String){
        CoroutineScope(Dispatchers.Main).launch {
            val exerciseList: ArrayList<ExerciseData> = DataGenerator.generateLoseWeightExercises(bicycleCrunches, burpees, jumpingJacks, highKnees, pushups)
            recyclerView.adapter = ExerciseAdapter(exerciseList)
        }
    }

    private fun gainMuscleRoutine(reps: String, benchPress: String, inclineBenchPress: String, squat: String, row: String, deadlift: String){
        CoroutineScope(Dispatchers.Main).launch {
            // Now you can use the passed-in variables within the coroutine
            val exerciseList: ArrayList<ExerciseData> = DataGenerator.generateGainMuscleExercises(reps, benchPress, inclineBenchPress, squat, row, deadlift)
            recyclerView.adapter = ExerciseAdapter(exerciseList)
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000 % 60).toString().padStart(2, '0')
                viewBinding.tvTimer.text = "$minutes:$seconds"
            }

            override fun onFinish() {
                viewBinding.tvTimer.text = "0:00"
                viewBinding.btnTimerFinish.isChecked = true
                isTimerRunning = false
                viewBinding.btnStartTimer.text = "Start"
            }
        }.start()
        isTimerRunning = true
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
        isTimerRunning = false
    }
}
