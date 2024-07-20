package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.fitmaster.databinding.ProfileBinding

class Profile : ComponentActivity() {
    private lateinit var viewBinding: ProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = ProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnLogout.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}
