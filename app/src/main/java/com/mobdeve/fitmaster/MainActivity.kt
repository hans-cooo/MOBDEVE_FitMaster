package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.fitmaster.databinding.StartingPageBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: StartingPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = StartingPageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.spRegister.setOnClickListener {

            val intent = Intent(this, RegisterAccount::class.java)
            startActivity(intent)
        }

        viewBinding.spLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}
