package com.mobdeve.fitmaster

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
    // View binding for accessing views in the layout
    private lateinit var viewBinding: LoginBinding

    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth

    // One Tap Sign-In client for Google Sign-In
    private var oneTapClient: SignInClient? = null

    // Google Sign-In request configuration
    private lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize view binding
        this.viewBinding = LoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        auth = FirebaseAuth.getInstance()

        /* TODO: fix
        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize One Tap Sign-In client
        oneTapClient = Identity.getSignInClient(this)

        // Configure Google Sign-In request
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

        // Navigate to RegisterAccount activity when the sign-up button is clicked
        viewBinding.btnSignUp.setOnClickListener {
            val intent = Intent(this, RegisterAccount::class.java)
            startActivity(intent)
        }

        // Display user information if already signed in
        showUser()
         */
        viewBinding.btnSignUp.setOnClickListener(){
            val intent = Intent(this, RegisterAccount::class.java)
            startActivity(intent)
            finish()
        }
    }


    // Function to handle Google Sign-In button click
    fun signingGoogle(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            signingGoogle()
        }
    }

    // Suspend function to initiate Google Sign-In
    private suspend fun signingGoogle() {
        // Start the sign-in process
        val result = oneTapClient?.beginSignIn(signInRequest)?.await()
        val intentSenderRequest = IntentSenderRequest.Builder(result!!.pendingIntent).build()
        // Launch the sign-in intent
        activityResultLauncher.launch(intentSenderRequest)
    }

    // Activity result launcher to handle the result of the sign-in intent
    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    // Get the sign-in credential from the result data
                    val credential = oneTapClient!!.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        // Create Firebase credential with the ID token
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        // Sign in with the Firebase credential
                        auth.signInWithCredential(firebaseCredential).addOnCompleteListener {
                            if (it.isSuccessful) {
                                // Hide progress bar and show success message
                                //viewBinding.progressBar.visibility = View.INVISIBLE TODO: fix
                                Toast.makeText(this, "Sign in Complete", Toast.LENGTH_LONG).show()
                                // Display user information
                                showUser()
                            }
                        }
                    }
                } catch (e: ApiException) {
                    // Handle sign-in failure
                    e.printStackTrace()
                }
            }
        }

    // Function to sign out the current user
    /* TODO: fix
    fun signOutUser(view: View) {
        // Sign out from Firebase
        Firebase.auth.signOut()
        // Show sign-out message and clear displayed user information
        Toast.makeText(this, "Sign Out", Toast.LENGTH_LONG).show()
        viewBinding.txtName.text = ""
        viewBinding.txtEmail.text = ""
        viewBinding.txtStatus.text = ""
        viewBinding.imageView.setImageBitmap(null)
    }
     */

    // Function to display user information if already signed in
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            showUser()
        }
    }

    // Function to display the current user's information
    private fun showUser() {
     /*   TODO: fix
        val user = Firebase.auth.currentUser
        user?.let {
            // Retrieve user details
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl
            val emailVerified = it.isEmailVerified

            // Display user details in the UI
            viewBinding.txtName.text = name
            viewBinding.txtEmail.text = email
            if (emailVerified) {
                viewBinding.txtStatus.text = "Verified Email"
            }

            // Load and display user profile picture
            var image: Bitmap? = null
            val imageURL = photoUrl.toString()
            val executorService = Executors.newSingleThreadExecutor()

            executorService.execute {
                try {
                    // Fetch the image from the URL
                    val `in` = java.net.URL(imageURL).openStream()
                    image = BitmapFactory.decodeStream(`in`)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            runOnUiThread {
                try {
                    // Wait for the image to be loaded
                    Thread.sleep(1000)
                    // Display the image in the ImageView
                    viewBinding.imageView.setImageBitmap(image)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

      */
    }
}
