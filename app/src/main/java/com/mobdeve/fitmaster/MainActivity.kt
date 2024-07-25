package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.fitmaster.databinding.StartingPageBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: StartingPageBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = StartingPageBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // If the user is already logged in, launch dashboard
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }

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
