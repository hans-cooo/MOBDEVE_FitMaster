package com.mobdeve.fitmaster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.mobdeve.fitmaster.databinding.RegisterAccountBinding

class RegisterAccount : AppCompatActivity() {
    private lateinit var viewBinding: RegisterAccountBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = RegisterAccountBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        auth = FirebaseAuth.getInstance()

        viewBinding.tvwLoginRedirect.setOnClickListener() {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        // Create an Account
        viewBinding.btnCreate.setOnClickListener() {
            val name = viewBinding.edtFullName.text.toString()
            val birthday = viewBinding.edtBirthday.text.toString()
            val email = viewBinding.edtEmail.text.toString()
            val password = viewBinding.edtPassword.text.toString()
            if(name.isEmpty() or birthday.isEmpty() or email.isEmpty() or password.isEmpty()){
                Toast.makeText(this, "Fill Incomplete Fields", Toast.LENGTH_LONG).show()
            }
            else if(password.length < 6){
                Toast.makeText(this, "Password should be at least 6 characters.", Toast.LENGTH_LONG).show()
            }
            else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ task: Task<AuthResult> ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Successfully Signed Up", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, Dashboard::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }else {
                        Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }



    }
}