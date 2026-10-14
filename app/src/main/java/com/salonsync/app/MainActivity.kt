package com.salonsync.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.salonsync.app.ui.navigation.AppNavHost
import com.salonsync.app.ui.theme.SalonSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SalonSyncTheme {
                AppNavHost()
            }
        }
    }
}
