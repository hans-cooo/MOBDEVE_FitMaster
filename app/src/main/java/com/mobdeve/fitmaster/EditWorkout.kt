package com.mobdeve.fitmaster

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.fitmaster.databinding.ActivityDashboardBinding
import com.mobdeve.fitmaster.databinding.EditWorkoutBinding

class EditWorkout : AppCompatActivity() {
    private lateinit var viewBinding: EditWorkoutBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = EditWorkoutBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(viewBinding.root)

        val email = this.intent.getStringExtra("email")
        this.recyclerView = viewBinding.maxWeightRecycler
    }
}