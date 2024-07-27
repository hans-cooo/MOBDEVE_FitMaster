package com.mobdeve.fitmaster

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.time.Duration

class DataGenerator {
    companion object {
        suspend fun generateDayStatuses(email: String): ArrayList<DayStatus> {
            val db = Firebase.firestore
            val statusRef = db.collection(MyFirestoreReferences.USER_PROGRESS_COLLECTION)
            val tempList = ArrayList<DayStatus>()

            try {
                val querySnapshot = statusRef.whereEqualTo("email", email).get().await()
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    // Retrieve the status for each day from Day1 to Day7
                    val days = listOf("Day1", "Day2", "Day3", "Day4", "Day5", "Day6", "Day7")
                    for (day in days) {
                        val dayStatus = document.getString(day) ?: "error"
                        tempList.add(DayStatus(day, dayStatus))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the error accordingly
            }
            return tempList
        }

        private fun getMETValue(exercise: String): Double{
            return when(exercise){
                "Jumping Jacks" -> 8.0
                "Push Ups" -> 8.0
                "Bicycle Crunches" -> 6.0
                "Burpees" -> 10.0
                "High Knees" -> 8.0
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
                // For exercises based on repetitions and weights, assume 1 minute per set
                calories = adjustedMET * userWeight * (exercise.repetitions.toInt() / 60.0)
            }

            return calories
        }

/*      TODO: Complete code
        fun calculateCalories(exercises: List<ExerciseData>, userWeight: Double, goal: String): Double {
            var totalCalories = 0.0

            for (exercise in exercises) {
                val met = getMETValue(exercise.name.lowercase())
                val durationInHours = exercise.duration / 60.0

                // Adjust MET value based on the weight lifted and repetitions
                val adjustedMET = if (goal == "gainMuscle") {
                    met + (exercise.weight.toInt() * exercise.repetitions.toInt() * 0.01)
                } else {
                    met
                }

                // Calculate calories burned
                val calories = adjustedMET * userWeight * durationInHours
                totalCalories += calories
            }

            // Adjust calories based on user goal if needed
            return totalCalories
        }
*/

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
