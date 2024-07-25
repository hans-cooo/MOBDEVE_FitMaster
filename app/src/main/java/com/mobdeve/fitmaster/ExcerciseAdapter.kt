package com.mobdeve.fitmaster

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.fitmaster.databinding.ExerciseBinding

class ExerciseAdapter(private val exercises: List<ExcerciseData>) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(private val binding: ExerciseBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(exercise: ExcerciseData) {
            binding.tvExerciseName.text = exercise.name
            binding.tvExerciseWeight.text = exercise.weight
            binding.tvExerciseReps.text = exercise.repetitions
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
