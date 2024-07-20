package com.mobdeve.fitmaster

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mobdeve.fitmaster.databinding.ActivityDashboardBinding

class Dashboard : AppCompatActivity() {
    private lateinit var viewBinding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        /*TODO:
           Implement data handler, data comes from firebase
           Complete RecyclerView Adapter (id:workoutDayRecycler) using workout_day.xml as the viewholder layout.
           Set imvStatus to status_(complete/error/empty).png depending on the workout status.
           Set btnDay background to button_(gray/blue).xml if workout day is (completed/incomplete)
           setonclicklistener on btnDay to start workout activity
           Figure out how Manage Workout functions
        */
    }
}