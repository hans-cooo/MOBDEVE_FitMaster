package com.mobdeve.fitmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mobdeve.fitmaster.databinding.ProfileBinding
import com.mobdeve.fitmaster.ui.theme.FitMasterTheme

class Profile : ComponentActivity() {
    private lateinit var viewBinding: ProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        this.viewBinding = ProfileBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.btnLogout.setOnClickListener(){

        }
    }
}
