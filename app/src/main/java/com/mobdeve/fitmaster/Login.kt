package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.fitmaster.databinding.LoginBinding

class Login : AppCompatActivity() {
    private lateinit var viewBinding: LoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = LoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnSignUp.setOnClickListener {
            val intent = Intent(this, RegisterAccount::class.java)
            startActivity(intent)
        }
    }
}