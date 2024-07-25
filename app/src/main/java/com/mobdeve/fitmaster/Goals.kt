package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.fitmaster.databinding.ActivityGoalsBinding

class Goals : AppCompatActivity() {

    private lateinit var viewBinding: ActivityGoalsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        val email = this.intent.getStringExtra("email")

        viewBinding.btnBeginner.setOnClickListener {
            if (viewBinding.btnBeginner.isChecked) {
                viewBinding.btnIntermediate.isChecked = false
                viewBinding.btnAdvanced.isChecked = false
            }
        }

        viewBinding.btnIntermediate.setOnClickListener {
            if (viewBinding.btnIntermediate.isChecked) {
                viewBinding.btnBeginner.isChecked = false
                viewBinding.btnAdvanced.isChecked = false
            }
        }

        viewBinding.btnAdvanced.setOnClickListener {
            if (viewBinding.btnAdvanced.isChecked) {
                viewBinding.btnBeginner.isChecked = false
                viewBinding.btnIntermediate.isChecked = false
            }
        }

        viewBinding.btnWeightLoss.setOnClickListener {
            if (viewBinding.btnWeightLoss.isChecked) {
                viewBinding.btnGainMuscles.isChecked = false
            }
        }

        viewBinding.btnGainMuscles.setOnClickListener {
            if (viewBinding.btnGainMuscles.isChecked) {
                viewBinding.btnWeightLoss.isChecked = false
            }
        }

        viewBinding.btnSave.setOnClickListener {

            val intent = Intent(this, Profile::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
            finish()
        }

    }
}