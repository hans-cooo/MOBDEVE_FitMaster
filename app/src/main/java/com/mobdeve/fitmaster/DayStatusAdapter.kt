package com.mobdeve.fitmaster

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class DayStatusAdapter(private val days: List<DayStatus>, private val email: String) : RecyclerView.Adapter<DayStatusViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayStatusViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.workout_day, parent, false)
        return DayStatusViewHolder(view, email)
    }

    override fun onBindViewHolder(holder: DayStatusViewHolder, position: Int) {
        holder.bindData(days.get(position))
    }

    override fun getItemCount() = days.size


}
