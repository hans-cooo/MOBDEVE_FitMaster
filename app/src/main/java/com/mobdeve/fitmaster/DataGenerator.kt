package com.mobdeve.fitmaster

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.time.Duration

class DataGenerator {
    companion object {
        suspend fun generateDayStatuses(email: String, uptoDay: Int): ArrayList<DayStatus> {
            val db = Firebase.firestore
            val statusRef = db.collection(MyFirestoreReferences.USER_PROGRESS_COLLECTION)
            val tempList = ArrayList<DayStatus>()

            try {
                val querySnapshot = statusRef.whereEqualTo("email", email).get().await()
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    // Retrieve the status for each day from Day1 to Day7
                    val range : IntRange = if(uptoDay < 7)
                        1..uptoDay
                    else
                        (uptoDay-6)..uptoDay
                    for (day in range) {
                        val dayString = "Day$day"
                        val dayStatus = document.getString(dayString) ?: "error"
                        tempList.add(DayStatus(dayString, dayStatus))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the error accordingly
            }
            return tempList
        }

        suspend fun getLastDay(email: String): Int{
            val db = Firebase.firestore
            val statusRef = db.collection(MyFirestoreReferences.USER_PROGRESS_COLLECTION)
            var lastDay = 0

            try{
                val querySnapshot = statusRef.whereEqualTo("email", email).get().await()
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    lastDay = document.getString(MyFirestoreReferences.LAST_DAY_FIELD).toString().toInt()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the error accordingly
            }
            return lastDay
        }

        private fun getMETValue(exercise: String): Double{
            return when(exercise){
                "Jumping Jacks" -> 6.5
                "Push Ups" -> 7.0
                "Bicycle Crunches" -> 6.0
                "Burpees" -> 8.0
                "High Knees" -> 6.5
                "Squats" -> 5.0
                "Rows" -> 4.0
                "Dumbbell Bench Press" -> 4.5
                "Inclined Bench Press" -> 4.5
                "Deadlift" -> 5.5
                "Jogging" -> 7.0
                else -> 0.0
            }
        }
        fun calculateCalories(exercise: ExerciseData, userWeight: Double, goal: String): Double {
            var calories = 0.0

            val met = getMETValue(exercise.name)
            Log.e("DataGenerator", "${exercise.name} , $met")

            // Adjust MET value based on the weight lifted and repetitions for muscle gain exercises
            val adjustedMET = if (goal == "gainMuscle" && exercise.weight.isNotEmpty() && exercise.repetitions.isNotEmpty()) {
                met + (exercise.weight.toInt() * exercise.repetitions.toInt() * 0.008)
            } else {
                met
            }

            // Calculate calories burned
            if (exercise.name.equals("Jogging", ignoreCase = true)) {
                val durationInHours = 15.0 / 60.0
                calories = adjustedMET * userWeight * durationInHours
            } else {
               
                        val durationInMinutes = exercise.repetitions.toInt() / 15.0
                        val durationInHours = durationInMinutes / 60.0
                        calories = adjustedMET * userWeight * durationInHours

            }

            return calories
        }

        // Generates lose weight exercises
        fun generateLoseWeightExercises(bicycleCrunches: String, burpees: String, jumpingJacks: String, highKnees: String, pushups: String): ArrayList<ExerciseData> {
            val exercises = ArrayList<ExerciseData>()
            exercises.add(ExerciseData(name = "Bicycle Crunches", weight = "N/A", repetitions = "$bicycleCrunches reps"))
            exercises.add(ExerciseData(name = "Burpees", weight = "N/A", repetitions = "$burpees reps"))
            exercises.add(ExerciseData(name = "Jumping Jacks", weight = "N/A", repetitions = "$jumpingJacks reps"))
            exercises.add(ExerciseData(name = "High Knees", weight = "N/A", repetitions = "$highKnees reps"))
            exercises.add(ExerciseData(name = "Push-ups", weight = "N/A", repetitions = "$pushups reps"))
            return exercises
        }

        // Generates gain muscle exercises
        fun generateGainMuscleExercises(reps: String, benchPress: String, inclineBenchPress: String, squat: String, row: String, deadlift: String): ArrayList<ExerciseData> {
            val exercises = ArrayList<ExerciseData>()
            exercises.add(ExerciseData(name = "Bench Press", weight = "$benchPress kg", repetitions = "$reps reps"))
            exercises.add(ExerciseData(name = "Inclined Bench Press", weight = "$inclineBenchPress kg", repetitions = "$reps reps"))
            exercises.add(ExerciseData(name = "Squats", weight = "$squat kg", repetitions = "$reps reps"))
            exercises.add(ExerciseData(name = "Rows", weight = "$row kg", repetitions = "$reps reps"))
            exercises.add(ExerciseData(name = "Deadlift", weight = "$deadlift kg", repetitions = "$reps reps"))
            return exercises
        }

    }
}
