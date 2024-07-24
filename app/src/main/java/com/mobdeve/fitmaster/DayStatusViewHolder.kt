package com.mobdeve.fitmaster

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class DayStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val btnDay: Button = itemView.findViewById(R.id.btnDay)
    private val imvStatus: ImageView = itemView.findViewById(R.id.imvStatus)

    fun bindData(dayStatus: DayStatus) {
        btnDay.text = dayStatus.day
        // TODO: Might have to change this if imv becomes a string
        imvStatus.setImageResource(dayStatus.status)

        btnDay.setOnClickListener {
            // Get the context and start WorkoutActivity
            val context = itemView.context
            val intent = Intent(context, WorkoutActivity::class.java)
            context.startActivity(intent)

            /* something like this
        if (imvStatus = "empty") {
            imvStatus.setImageResource(R.drawable.status_empty)
        } else if (imvStatus = "complete") {
            imvStatus.setImageResource(R.drawable.status_complete)
        } else {
            imvStatus.setImageResource(R.drawable.status_error)
        }
         */
        }
    }
}