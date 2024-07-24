package com.mobdeve.fitmaster

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val email = this.intent.getStringExtra("email")
        this.recyclerView = viewBinding.workoutDayRecycler
        this.recyclerView.layoutManager = LinearLayoutManager(this)
        //TODO: Complete generateDayStatuses function in DataGenerator.kt and make it work with email
        CoroutineScope(Dispatchers.Main).launch {
            val dayList: ArrayList<DayStatus> = DataGenerator.generateDayStatuses(email.toString())
            recyclerView.adapter = DayStatusAdapter(dayList)
        }



    }


}
