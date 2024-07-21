package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.fitmaster.databinding.ActivityDashboardBinding

class Dashboard : AppCompatActivity() {
    private lateinit var viewBinding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)




        viewBinding.imvProfile.setOnClickListener(){
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)

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