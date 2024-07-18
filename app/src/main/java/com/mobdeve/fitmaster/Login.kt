package com.mobdeve.fitmaster

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mobdeve.fitmaster.databinding.LoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.Executors

class Login : AppCompatActivity() {
    private lateinit var viewBinding: LoginBinding
    private lateinit var auth: FirebaseAuth
    private var oneTapClient: SignInClient? = null
    private lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = LoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        auth = Firebase.auth
        oneTapClient = Identity.getSignInClient(this)

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        viewBinding.btnSignUp.setOnClickListener {
            val intent = Intent(this, RegisterAccount::class.java)
            startActivity(intent)
        }

        showUser() // Call the showUser function to display user details if already signed in
    }

    fun signingGoogle (view: View)
    {
        CoroutineScope(Dispatchers.Main).launch{
            signingGoogle()
        }
    }

    private suspend fun signingGoogle ()
    {
        val result = oneTapClient?. beginSignIn()?.await()
        val intentSenderRequest = IntentSenderRequest.Builder(result!!.pendingIntent).build()
        activityResultLauncher.launch(intentSenderRequest)
    }

    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ){ result ->
           if(result.resultCode == RESULT_OK){
               try{
                   val credential = oneTapClient!!.getSignInCredentialFromIntent(result.data)
                   val idToken= credential.googleIdToken
                   if (idToken != null){
                       val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                       auth.signInWithCredential(firebaseCredential).addOnCompleteListener{
                           if (it.isSuccessful){
                               binding.progressBar.visibility = View.INVISIBLE
                               Toast.makeText(this, "Sign in Complete", Toast.LENGTH_LONG)
                               showUser()
                           }
                       }
                   }


               } catch (e: ApiException){
                   e.printStackTrace()
               }
           }

        }


    override fun onStart()
    {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) run{
            showUser()
        }
    }

    private fun showUser() {
        val user = Firebase.auth.currentUser
        user?.let {
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl
            val emailVerified = it.isEmailVerified

            viewBinding.txtName.text = name
            viewBinding.txtEmail.text = email
            if (emailVerified) {
                viewBinding.txtStatus.text = "Verified Email"
            }

            var image: Bitmap? = null
            val imageURL = photoUrl.toString()
            val executorService = Executors.newSingleThreadExecutor()

            executorService.execute {
                try {
                    val `in` = java.net.URL(imageURL).openStream()
                    image = BitmapFactory.decodeStream(`in`)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            runOnUiThread {
                try {
                    Thread.sleep(1000)
                    viewBinding.imageView.setImageBitmap(image)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
