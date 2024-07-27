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
    private var pageNo = 1
    private var lastDay = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        email = this.intent.getStringExtra("email").toString()
        this.recyclerView = viewBinding.workoutDayRecycler
        this.recyclerView.layoutManager = LinearLayoutManager(this)

        // Populate recyclerview
        CoroutineScope(Dispatchers.Main).launch {
            lastDay = DataGenerator.getLastDay(email)
            generateRecycler(lastDay)
            // Configure page buttons
            viewBinding.btnPageLeft.setImageResource(R.drawable.triangle_right_gray)
            if(lastDay <= 7)
                viewBinding.btnPageRight.setImageResource(R.drawable.triangle_right_gray)
        }

        // Next page
        viewBinding.btnPageRight.setOnClickListener(){
            if(lastDay > pageNo*7){
                pageNo += 1
                generateRecycler(lastDay-7*(pageNo-1))
                viewBinding.tvwPageNo.text = pageNo.toString()
                viewBinding.btnPageLeft.setImageResource(R.drawable.triangle_right_blue)
                if(lastDay <= pageNo*7)
                    viewBinding.btnPageRight.setImageResource(R.drawable.triangle_right_gray)
            }
        }
        // Previous page
        viewBinding.btnPageLeft.setOnClickListener(){
            if(pageNo > 1){
                pageNo -= 1
                generateRecycler(lastDay-7*(pageNo-1))
                viewBinding.tvwPageNo.text = pageNo.toString()
                viewBinding.btnPageRight.setImageResource(R.drawable.triangle_right_blue)
                if(pageNo == 1)
                    viewBinding.btnPageLeft.setImageResource(R.drawable.triangle_right_gray)
            }
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
                lastDay = DataGenerator.getLastDay(email) + 1
                val updatedData = mutableMapOf<String, Any>()
                updatedData["Day$lastDay"] = "empty"
                updatedData[MyFirestoreReferences.LAST_DAY_FIELD] = (lastDay).toString()

                statusRef.whereEqualTo("email", email).get().addOnSuccessListener { documents ->
                    if (documents != null && !documents.isEmpty) {
                        val document = documents.first()
                        val documentId = document.id
                        statusRef.document(documentId).update(updatedData)
                    }
                    pageNo = 1
                    viewBinding.tvwPageNo.text = "1"
                    generateRecycler(lastDay)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            generateRecycler(lastDay-7*(pageNo-1))
        }
    }

    private fun generateRecycler(day:Int){
        CoroutineScope(Dispatchers.Main).launch {
            val dayList: ArrayList<DayStatus> = DataGenerator.generateDayStatuses(email, day)
            recyclerView.adapter = DayStatusAdapter(dayList, email)
        }
    }

}
