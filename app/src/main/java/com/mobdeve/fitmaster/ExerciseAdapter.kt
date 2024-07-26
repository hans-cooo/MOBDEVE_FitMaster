package com.mobdeve.fitmaster

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.fitmaster.databinding.ExerciseBinding

class ExerciseAdapter(private val exercises: List<ExerciseData>) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(private val binding: ExerciseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(exercise: ExerciseData) {
            binding.tvExerciseName.text = exercise.name
            if(exercise.weight.isNotBlank())
                binding.tvExerciseWeight.text = "${exercise.weight} kg"
            else {
                binding.tvExerciseWeight.text = ""
            }

            binding.tvExerciseReps.text = "${exercise.repetitions} reps"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bindData(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size
}
