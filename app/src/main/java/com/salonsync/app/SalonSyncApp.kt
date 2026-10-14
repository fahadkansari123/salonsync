package com.salonsync.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class SalonSyncApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (FirebaseApp.getApps(this).isEmpty()) {
            try {
                val options = FirebaseOptions.Builder()
                    .setApiKey("AIzaSyAW9KzyIZLXxK5SYZl0A9LG_XNQMLaHXfw")
                    .setApplicationId("1:466380869991:android:a1b2c3d4e5f67890")
                    .setDatabaseUrl("https://salonsync-c519b-default-rtdb.firebaseio.com")
                    .setProjectId("salonsync-c519b")
                    .build()
                FirebaseApp.initializeApp(this, options)
            } catch (e: Exception) {
                FirebaseApp.initializeApp(this)
            }
        }
    }
}
