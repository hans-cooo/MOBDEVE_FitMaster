package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.mobdeve.fitmaster.databinding.ActivityDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Dashboard : AppCompatActivity() {
    private lateinit var viewBinding: ActivityDashboardBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        email = this.intent.getStringExtra("email").toString()
        this.recyclerView = viewBinding.workoutDayRecycler
        this.recyclerView.layoutManager = LinearLayoutManager(this)

        // Populate recyclerview
        CoroutineScope(Dispatchers.Main).launch {
            generateRecycler(DataGenerator.getLastDay(email))
        }

        viewBinding.imvProfile.setOnClickListener(){
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        viewBinding.btnEdit.setOnClickListener(){
            val intent = Intent(this, EditWorkout::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }

        viewBinding.btnAdd.setOnClickListener(){
            val db = Firebase.firestore
            val statusRef = db.collection(MyFirestoreReferences.USER_PROGRESS_COLLECTION)
            CoroutineScope(Dispatchers.Main).launch {
                val lastDay = DataGenerator.getLastDay(email) + 1
                val updatedData = mutableMapOf<String, Any>()
                updatedData["Day$lastDay"] = "empty"
                updatedData[MyFirestoreReferences.LAST_DAY_FIELD] = (lastDay).toString()

                statusRef.whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                    if (documents != null && !documents.isEmpty) {
                        val document = documents.first()
                        val documentId = document.id
                        statusRef.document(documentId).update(updatedData)
                    }
                    generateRecycler(lastDay)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            generateRecycler(DataGenerator.getLastDay(email))
        }
    }

    private fun generateRecycler(day:Int){
        CoroutineScope(Dispatchers.Main).launch {
            val dayList: ArrayList<DayStatus> = DataGenerator.generateDayStatuses(email, day)
            recyclerView.adapter = DayStatusAdapter(dayList, email)
        }
    }
/*
    private fun getLastDay(email:String, statusRef: CollectionReference):Int{
        var lastDay = 2
        val query = statusRef.whereEqualTo("email", email)
        query.get().addOnSuccessListener { documents ->
            if (documents != null && !documents.isEmpty) {
                val document = documents.first()
                lastDay = document.getString(MyFirestoreReferences.LAST_DAY_FIELD).toString().toInt()
            } else {
                Toast.makeText(this, "No user found with this email", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error fetching profile: ${exception.message}", Toast.LENGTH_LONG).show()
        }

        /*
        CoroutineScope(Dispatchers.Main).launch{
            try {
                val querySnapshot = statusRef.whereEqualTo("email", email).get().await()
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    // Retrieve the last workout day ID
                    dayStatus = document.getString(MyFirestoreReferences.LAST_DAY_FIELD).toString().toInt()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the error accordingly
            }
        }

         */
        return lastDay
    }

 */

}
