package com.mobdeve.fitmaster

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
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
        viewBinding.tvwWorkout.text = "Workout — ${day}"

        val db = Firebase.firestore
        val usersRef = db.collection(MyFirestoreReferences.USERS_COLLECTION)
        val workoutRef = db.collection(MyFirestoreReferences.USER_WORKOUT_COLLECTION)
        val query = usersRef.whereEqualTo(MyFirestoreReferences.EMAIL_FIELD, email)
        val workoutQuery = workoutRef.whereEqualTo(MyFirestoreReferences.EMAIL_FIELD, email)

        var reps: String? = null

        var level: String
        var goal = ""
        var userWeight = 0.0

        // Start workout timer
        startWorkoutTimer()

        // Sets workout routine based on user's level, goal, and set weights
        query.get().addOnSuccessListener { documents ->
            val document = documents.first()
            level = document.getString(MyFirestoreReferences.LEVEL_FIELD).toString()
            goal = document.getString(MyFirestoreReferences.GOAL_FIELD).toString()
            userWeight = document.getString(MyFirestoreReferences.WEIGHT_FIELD).toString().toDouble()

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

                val adapter = ExerciseAdapter(exercises)
                viewBinding.exerciseRecycler.adapter = adapter
                totalExercises = adapter.itemCount + 1

            }
        }

        viewBinding.btnFinWorkout.setOnClickListener(){
            var exercisesCompleted = 0
            for(i in 0..<(totalExercises-1)){
                if(viewBinding.exerciseRecycler.layoutManager!!.findViewByPosition(i)?.findViewById<ToggleButton>(R.id.tbnStatus)?.isChecked!!)
                    exercisesCompleted += 1
            }
            if(viewBinding.btnTimerFinish.isChecked)
                exercisesCompleted += 1

            // TODO: Make this go to summary and complete any other logic relating to the workout ending
            // remember to finish() after the intent and finish() when going from summary to dashboard
            if(exercisesCompleted == totalExercises){

                var totalCalories = 0.0
                totalCalories += DataGenerator.calculateCalories(ExerciseData("Jogging", "", ""), userWeight, goal)
                Log.e("WorkoutActivity", "checking current calories (jogging): $totalCalories")
                for (exercise in exercises){
                    totalCalories += DataGenerator.calculateCalories(exercise, userWeight, goal)
                    Log.e("WorkoutActivity", "checking current calories: $totalCalories")
                }

                // Capture the end time in milliseconds
                val endTime = System.currentTimeMillis()
                // Calculate the duration in milliseconds
                val duration = endTime - startTime
                val intent = Intent(this, Summary::class.java)
                intent.putExtra("email", email)
                intent.putExtra("calories", totalCalories)
                intent.putExtra("WORKOUT_DURATION", duration)
                startActivity(intent)
                finish()
            }
            else
                Toast.makeText(this, "Finish all exercises first!", Toast.LENGTH_LONG).show()
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

        // Restore progress if exists
        //restoreProgress()
    }

    private fun startWorkoutTimer() {
        // Capture the start time in milliseconds
        startTime = System.currentTimeMillis()
    }
    /*
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
    */

/*
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
*/
    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000 % 60).toString().padStart(2, '0')
                viewBinding.tvTimer.text = "$minutes:$seconds"
            }

            override fun onFinish() {
                isTimerRunning = false
                viewBinding.tvTimer.text = "0:00"
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
        val exercisesCompleted = getCompletedExercises()
        editor.putStringSet("completedExercises", exercisesCompleted)
        editor.apply()
    }

    private fun restoreProgress() {
        isTimerRunning = sharedPreferences.getBoolean("isTimerRunning", false)
        timeLeftInMillis = sharedPreferences.getLong("timeLeftInMillis", 900000L)
        isWorkoutStarted = sharedPreferences.getBoolean("isWorkoutStarted", false)
        startTime = sharedPreferences.getLong("startTime", 0L)
        val completedExercises = sharedPreferences.getStringSet("completedExercises", emptySet())!!

        if (isTimerRunning) {
            startTimer()
            viewBinding.btnStartTimer.text = "Pause"
        } else {
            viewBinding.tvTimer.text = "${timeLeftInMillis / 1000 / 60}:${(timeLeftInMillis / 1000 % 60).toString().padStart(2, '0')}"
        }

        if (isWorkoutStarted) {
            viewBinding.btnStartWorkout.isEnabled = false
        }

        restoreCompletedExercises(completedExercises)
    }

    private fun getCompletedExercises(): Set<String> {
        val completedExercises = mutableSetOf<String>()
        for (i in 0 until recyclerView.childCount) {
            val view = recyclerView.layoutManager!!.findViewByPosition(i)
            val toggleButton = view?.findViewById<ToggleButton>(R.id.tbnStatus)
            if (toggleButton?.isChecked == true) {
                completedExercises.add(exercises[i].name)
            }
        }
        if (viewBinding.btnTimerFinish.isChecked) {
            completedExercises.add("TimerFinish")
        }
        return completedExercises
    }

    private fun restoreCompletedExercises(completedExercises: Set<String>) {
        for (i in 0 until recyclerView.childCount) {
            val view = recyclerView.layoutManager!!.findViewByPosition(i)
            val toggleButton = view?.findViewById<ToggleButton>(R.id.tbnStatus)
            if (completedExercises.contains(exercises[i].name)) {
                toggleButton?.isChecked = true
            }
        }
        if (completedExercises.contains("TimerFinish")) {
            viewBinding.btnTimerFinish.isChecked = true
        }
    }
}
