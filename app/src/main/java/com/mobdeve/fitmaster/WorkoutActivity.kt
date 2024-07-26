package com.mobdeve.fitmaster

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ToggleButton
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
    private var totalExercises: Int = 0
    private var isTimerRunning = false
    private var timeLeftInMillis = 900000L
    private var isWorkoutStarted = false
    private var startTime = 0L
    private val exercises = ArrayList<ExerciseData>()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = ActivityWorkoutBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        sharedPreferences = getSharedPreferences("WorkoutPrefs", Context.MODE_PRIVATE)

        val email = this.intent.getStringExtra("email")
        val day = this.intent.getStringExtra("day") // The day that was pressed

        val db = Firebase.firestore
        val usersRef = db.collection(MyFirestoreReferences.USERS_COLLECTION)
        val workoutRef = db.collection(MyFirestoreReferences.USER_WORKOUT_COLLECTION)
        val query = usersRef.whereEqualTo(MyFirestoreReferences.EMAIL_FIELD, email)
        val workoutQuery = workoutRef.whereEqualTo(MyFirestoreReferences.EMAIL_FIELD, email)

        var reps: String? = null
        // Start workout timer
        startWorkoutTimer()

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
                val exerciseList = when (goal) {
                    "loseWeight" -> MyFirestoreReferences.EXERCISE_LIST_LOSE_WEIGHT
                    "gainMuscle" -> MyFirestoreReferences.EXERCISE_LIST_GAIN_MUSCLE
                    else -> MyFirestoreReferences.EXERCISE_LIST
                }
                // Creates data for recyclerview
                for (exerciseName in exerciseList) {
                    val value = document2.getString(exerciseName).toString()
                    val name = MyFirestoreReferences.getName(exerciseName)
                    if (goal == "loseWeight") {
                        exercises.add(ExerciseData(name, "", value))
                    } else {
                        exercises.add(ExerciseData(name, value, reps.toString()))
                    }
                }

                val adapter = ExerciseAdapter(exercises)
                viewBinding.exerciseRecycler.adapter = adapter
                totalExercises = adapter.itemCount + 1

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

        viewBinding.btnFinWorkout.setOnClickListener() {
            var exercisesCompleted = 0
            for (i in 0..<(totalExercises - 1)) {
                if (viewBinding.exerciseRecycler.layoutManager!!.findViewByPosition(i)
                        ?.findViewById<ToggleButton>(R.id.tbnStatus)?.isChecked!!
                )
                    exercisesCompleted += 1
            }
            if (viewBinding.btnTimerFinish.isChecked)
                exercisesCompleted += 1

            // TODO: Make this go to summary and complete any other logic relating to the workout ending
            // remember to finish() after the intent and finish() when going from summary to dashboard
            if (exercisesCompleted == totalExercises) {
                finishWorkout()
                val intent = Intent(this, Summary::class.java)
                startActivity(intent)
                finish()
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
        restoreProgress()
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

    private fun loseWeightRoutine(
        bicycleCrunches: String,
        burpees: String,
        jumpingJacks: String,
        highKnees: String,
        pushups: String
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val exerciseList: ArrayList<ExerciseData> = DataGenerator.generateLoseWeightExercises(
                bicycleCrunches,
                burpees,
                jumpingJacks,
                highKnees,
                pushups
            )
            recyclerView.adapter = ExerciseAdapter(exerciseList)
        }
    }

    private fun gainMuscleRoutine(
        reps: String,
        benchPress: String,
        inclineBenchPress: String,
        squat: String,
        row: String,
        deadlift: String
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            // Now you can use the passed-in variables within the coroutine
            val exerciseList: ArrayList<ExerciseData> = DataGenerator.generateGainMuscleExercises(
                reps,
                benchPress,
                inclineBenchPress,
                squat,
                row,
                deadlift
            )
            recyclerView.adapter = ExerciseAdapter(exerciseList)
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            @SuppressLint("SetTextI18n")
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

    override fun onPause() {
        super.onPause()
        saveProgress()
    }

    private fun saveProgress() {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isTimerRunning", isTimerRunning)
        editor.putLong("timeLeftInMillis", timeLeftInMillis)
        editor.putBoolean("isWorkoutStarted", isWorkoutStarted)
        editor.putLong("startTime", startTime)

        // Save completed exercises
        val completedExercises = mutableMapOf<Int, Boolean>() // Map index to checked state
        for (i in 0 until recyclerView.childCount) {
            val view = recyclerView.getChildAt(i)
            val toggleButton = view.findViewById<ToggleButton>(R.id.tbnStatus)
            completedExercises[i] = toggleButton.isChecked
        }
        editor.putStringSet(
            "completedExercises",
            completedExercises.map { "${it.key}:${it.value}" }.toSet()
        )
        editor.apply()
    }

    private fun restoreProgress() {
        // Restore timer and workout start state
        isTimerRunning = sharedPreferences.getBoolean("isTimerRunning", false)
        timeLeftInMillis = sharedPreferences.getLong("timeLeftInMillis", 900000L)
        isWorkoutStarted = sharedPreferences.getBoolean("isWorkoutStarted", false)
        startTime = sharedPreferences.getLong("startTime", 0L)

        // Restore completed exercises
        val completedExercises =
            sharedPreferences.getStringSet("completedExercises", emptySet()) ?: emptySet()
        val exerciseStates = completedExercises.associate { entry ->
            // Ensure each entry is in the format "index:state"
            val parts = entry.split(":", limit = 2)
            (if (parts.size == 2) {
                val index = parts[0].toIntOrNull()
                val state = parts[1].toBoolean()
                index?.let { it to state }
            } else {
                null
            })!!
        }.filterKeys { it != null } // Ensure valid indices

        // Update UI based on the restored states
        if (isTimerRunning) {
            startTimer()
            viewBinding.btnStartTimer.text = "Pause"
        } else {
            viewBinding.tvTimer.text = "${timeLeftInMillis / 1000 / 60}:${
                (timeLeftInMillis / 1000 % 60).toString().padStart(2, '0')
            }"
        }

        if (isWorkoutStarted) {
            viewBinding.btnStartWorkout.isEnabled = false
        }

        restoreCompletedExercises(exerciseStates)
    }

    private fun restoreCompletedExercises(exerciseStates: Map<Int, Boolean>) {
        // Ensure the exercise list and recycler view are properly synchronized
        for ((index, isChecked) in exerciseStates) {
            if (index in 0 until recyclerView.childCount) {
                val view = recyclerView.getChildAt(index)
                val toggleButton = view.findViewById<ToggleButton>(R.id.tbnStatus)
                toggleButton?.isChecked = isChecked
            }
        }
        if (exerciseStates.containsKey(totalExercises) && exerciseStates[totalExercises] == true) {
            viewBinding.btnTimerFinish.isChecked = true
        }
    }
}
