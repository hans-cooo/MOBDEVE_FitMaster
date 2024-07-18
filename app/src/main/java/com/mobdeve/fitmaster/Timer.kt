package com.mobdeve.fitmaster

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.fitmaster.databinding.TimerBinding

class Timer : AppCompatActivity() {
    private lateinit var viewBinding: TimerBinding

    private var timeInSeconds = 0
    private var isRunning = false
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = TimerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        handler = Handler(Looper.getMainLooper())

        viewBinding.btnStart.setOnClickListener {
            startTimer()
        }

        viewBinding.btnPause.setOnClickListener {
            pauseTimer()
        }

        viewBinding.btnPause2.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        if (!isRunning) {
            isRunning = true
            handler.postDelayed(timerRunnable, 1000)
        }
    }

    private fun pauseTimer() {
        isRunning = false
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        isRunning = false
        handler.removeCallbacks(timerRunnable)
        timeInSeconds = 0
        updateTimerDisplay()
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                timeInSeconds++
                updateTimerDisplay()
                handler.postDelayed(this, 1000)
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun updateTimerDisplay() {
        val minutes = timeInSeconds / 60
        val seconds = timeInSeconds % 60
        viewBinding.tvwTime.text = String.format("%d:%02d", minutes, seconds)
    }
}
