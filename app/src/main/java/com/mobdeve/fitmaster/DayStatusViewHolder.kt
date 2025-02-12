package com.mobdeve.fitmaster

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class DayStatusViewHolder(itemView: View, private val email: String) : RecyclerView.ViewHolder(itemView) {
    private val btnDay: Button = itemView.findViewById(R.id.btnDay)
    private val imvStatus: ImageView = itemView.findViewById(R.id.imvStatus)

    fun bindData(dayStatus: DayStatus) {
        btnDay.text = dayStatus.day
        imvStatus.setImageResource(getStatusDrawable(dayStatus.status))
        if(dayStatus.status == "empty")
            btnDay.setBackgroundResource(R.drawable.button_blue)

        btnDay.setOnClickListener {

            val context = itemView.context
            val intent = Intent(context, WorkoutActivity::class.java)
            intent.putExtra("email", email)
            intent.putExtra("day", dayStatus.day) // Passes name of the day (Day1) to WorkoutActivity
            context.startActivity(intent)

        }
    }

    private fun getStatusDrawable(status: String): Int {
        return when (status) {
            "complete" -> R.drawable.status_complete
            "empty" -> R.drawable.status_empty
            else -> R.drawable.status_error
        }
    }
}