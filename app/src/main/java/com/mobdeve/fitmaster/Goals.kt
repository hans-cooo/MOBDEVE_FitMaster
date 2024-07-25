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

        viewBinding.btnSave.setOnClickListener {
            //TODO: Implement save feature
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
            finish()
        }

    }
}