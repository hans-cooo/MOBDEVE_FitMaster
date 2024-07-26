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
                        tempList.add(DayStatus(day, dayStatus))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the error accordingly
            }
            return tempList
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
