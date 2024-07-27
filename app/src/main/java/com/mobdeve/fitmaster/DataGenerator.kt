package com.mobdeve.fitmaster

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
                MyFirestoreReferences.JUMPING_JACKS_FIELD -> 8.0
                MyFirestoreReferences.PUSHUPS_FIELD -> 8.0
                MyFirestoreReferences.BICYCLE_CRUNCHES -> 6.0
                MyFirestoreReferences.BURPEES_FIELD -> 10.0
                MyFirestoreReferences.HIGH_KNEES_FIELD -> 8.0
                MyFirestoreReferences.SQUATS_FIELD -> 5.0
                MyFirestoreReferences.ROWS_FIELD -> 4.0
                MyFirestoreReferences.DUMBELL_BENCH_PRESS_FIELD -> 4.5
                MyFirestoreReferences.INCLINED_BENCH_PRESS_FIELD -> 4.5
                MyFirestoreReferences.DEADLIFT_FIELD -> 5.5
                else -> 0.0
            }
        }
        fun calculateCalories(exercise: ExerciseData, userWeight: Double, goal: String): Double {
            var calories = 0.0

            val met = getMETValue(exercise.name.lowercase())

            // Adjust MET value based on the weight lifted and repetitions for muscle gain exercises
            val adjustedMET = if (goal == "gainMuscle" && exercise.weight.isNotEmpty() && exercise.repetitions.isNotEmpty()) {
                met + (exercise.weight.toInt() * exercise.repetitions.toInt() * 0.01)
            } else {
                met
            }

            // Calculate calories burned
            if (exercise.name.equals("jogging", ignoreCase = true)) {
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
