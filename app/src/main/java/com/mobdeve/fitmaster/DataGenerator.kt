package com.mobdeve.fitmaster

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

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
                        tempList.add(DayStatus(day, getStatusDrawable(dayStatus)))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the error accordingly
            }
            return tempList
        }

        fun getStatusDrawable(status: String): Int {
            return when (status) {
                "complete" -> R.drawable.status_complete
                "empty" -> R.drawable.status_empty
                else -> R.drawable.status_error
            }
        }

        // Generates lose weight exercises
        fun generateLoseWeightExercises(bicycleCrunches: String, burpees: String, jumpingJacks: String, highKnees: String, pushups: String): ArrayList<ExcerciseData> {
            val exercises = ArrayList<ExcerciseData>()
            exercises.add(ExcerciseData(name = "Bicycle Crunches", weight = "N/A", repetitions = "$bicycleCrunches reps"))
            exercises.add(ExcerciseData(name = "Burpees", weight = "N/A", repetitions = "$burpees reps"))
            exercises.add(ExcerciseData(name = "Jumping Jacks", weight = "N/A", repetitions = "$jumpingJacks reps"))
            exercises.add(ExcerciseData(name = "High Knees", weight = "N/A", repetitions = "$highKnees reps"))
            exercises.add(ExcerciseData(name = "Push-ups", weight = "N/A", repetitions = "$pushups reps"))
            return exercises
        }

        // Generates gain muscle exercises
        fun generateGainMuscleExercises(reps: String, benchPress: String, inclineBenchPress: String, squat: String, row: String, deadlift: String): ArrayList<ExcerciseData> {
            val exercises = ArrayList<ExcerciseData>()
            exercises.add(ExcerciseData(name = "Bench Press", weight = "$benchPress kg", repetitions = "$reps reps"))
            exercises.add(ExcerciseData(name = "Inclined Bench Press", weight = "$inclineBenchPress kg", repetitions = "$reps reps"))
            exercises.add(ExcerciseData(name = "Squats", weight = "$squat kg", repetitions = "$reps reps"))
            exercises.add(ExcerciseData(name = "Rows", weight = "$row kg", repetitions = "$reps reps"))
            exercises.add(ExcerciseData(name = "Deadlift", weight = "$deadlift kg", repetitions = "$reps reps"))
            return exercises
        }
    }
}
