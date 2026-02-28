package com.seraphim.app.yxsg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.seraphim.app.yxsg.navigation.MainNavHost
import com.seraphim.app.yxsg.ui.theme.DelicaciesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            DelicaciesTheme {
                MainNavHost()
            }
        }
    }
}