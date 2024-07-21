package com.mobdeve.fitmaster

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.fitmaster.databinding.WorkoutDayBinding

// Data class to represent each workout day item
data class WorkoutDay(val day: String, val status: String)

class WorkoutDayAdapter(
    private val workoutDays: List<WorkoutDay>, // List of WorkoutDay items to display
    private val onClick: (WorkoutDay) -> Unit // Lambda function to handle item clicks
) : RecyclerView.Adapter<WorkoutDayAdapter.WorkoutDayViewHolder>() {

    // ViewHolder class for binding each item in the RecyclerView
    inner class WorkoutDayViewHolder(private val binding: WorkoutDayBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("UseCompatLoadingForDrawables") // Suppress lint warning for using getDrawable
        fun bind(workoutDay: WorkoutDay) {
            // Set the text for the day button
            binding.btnDay.text = workoutDay.day

            // Set the status image based on the workout status
            binding.imvStatus.setImageResource(
                when (workoutDay.status) {
                    "complete" -> R.drawable.status_complete
                    "error" -> R.drawable.status_error
                    "empty" -> R.drawable.status_empty
                    else -> R.drawable.status_empty
                }
            )

            // Set the button background color based on the workout status
            binding.btnDay.background = binding.root.context.getDrawable(
                if (workoutDay.status == "complete") R.drawable.button_blue else R.drawable.button_gray
            )

            // Handle button clicks
            binding.btnDay.setOnClickListener {
                onClick(workoutDay) // Invoke the onClick lambda function with the clicked WorkoutDay item
            }
        }
    }

    // Inflate the item layout and create a ViewHolder for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutDayViewHolder {
        val binding = WorkoutDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutDayViewHolder(binding)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: WorkoutDayViewHolder, position: Int) {
        holder.bind(workoutDays[position])
    }

    // Return the total number of items in the list
    override fun getItemCount(): Int = workoutDays.size
}
