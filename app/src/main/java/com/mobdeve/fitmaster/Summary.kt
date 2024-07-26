package com.mobdeve.fitmaster

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.fitmaster.databinding.ActivitySummaryBinding
import androidx.activity.enableEdgeToEdge


class Summary : AppCompatActivity() {

    private lateinit var viewBinding: ActivitySummaryBinding

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Retrieve the workout duration from the Intent
        val duration = intent.getLongExtra("WORKOUT_DURATION", 0L)
        // Convert the duration to minutes and seconds
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        val formattedDuration = String.format("%d:%02d", minutes, seconds)

        // Display the duration
        viewBinding.totalTimeText.setText("Total Time: \n$formattedDuration")
    }
}


