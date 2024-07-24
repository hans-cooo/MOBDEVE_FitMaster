package com.mobdeve.fitmaster

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class DataGenerator {
    companion object {
        val db = Firebase.firestore
        val statusRef = db.collection(MyFirestoreReferences.USER_PROGRESS_COLLECTION)

        suspend fun generateDayStatuses(email: String): ArrayList<DayStatus> {
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
    }
}
