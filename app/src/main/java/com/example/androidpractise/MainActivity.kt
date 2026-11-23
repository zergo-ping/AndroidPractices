package com.example.androidpractise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.androidpractise.navigation.AppNavHost
import com.example.androidpractise.ui.theme.AndroidPractiseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidPractiseTheme {
                AppNavHost()
            }
        }
    }
}
