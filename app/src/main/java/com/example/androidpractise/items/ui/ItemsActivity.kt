package com.example.androidpractise.items.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.androidpractise.navigation.AppNavHost
import com.example.androidpractise.ui.theme.AndroidPractiseTheme

class ItemsActivity : ComponentActivity() {
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