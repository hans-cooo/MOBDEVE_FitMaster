package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.fitmaster.databinding.ActivityDashboardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        CoroutineScope(Dispatchers.Main).launch {
            val dayList: ArrayList<DayStatus> = DataGenerator.generateDayStatuses(email)
            recyclerView.adapter = DayStatusAdapter(dayList, email)
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

    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            val dayList: ArrayList<DayStatus> = DataGenerator.generateDayStatuses(email)
            recyclerView.adapter = DayStatusAdapter(dayList, email)
        }
    }


}
