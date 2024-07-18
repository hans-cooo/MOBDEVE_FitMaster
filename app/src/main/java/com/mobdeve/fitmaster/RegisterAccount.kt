package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.fitmaster.databinding.RegisterAccountBinding

class RegisterAccount : AppCompatActivity() {
    private lateinit var viewBinding: RegisterAccountBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = RegisterAccountBinding.inflate(layoutInflater)
        setContentView(R.layout.register_account)

        viewBinding.btnLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}