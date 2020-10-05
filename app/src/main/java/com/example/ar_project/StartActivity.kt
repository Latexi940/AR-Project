package com.example.ar_project

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_start.*


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val prefs: SharedPreferences = getPreferences(MODE_PRIVATE)

        val prefsEdit: SharedPreferences.Editor = prefs.edit()

        val gson = Gson()

        val retrivedJson = prefs.getString("userProfile", "no user")

        //Uncomment this to clear user data
        //prefsEdit.clear()
        //prefsEdit.apply()


        if (retrivedJson != "no user") {
            val userProfile = gson.fromJson(retrivedJson, User::class.java)
            Log.i("ARPROJECT", "User profile found: $userProfile")
            val intent = Intent(this@StartActivity, MainActivity::class.java)
            intent.putExtra("userProfile", userProfile)
            startActivity(intent)
        } else {
            Log.i("ARPROJECT", "User profile not found. Please create a new one.")
            usernameField.isEnabled = true
            startButton.isEnabled = true
        }


        startButton.setOnClickListener {
            if (usernameField.text != null) {

                val newUser = User(usernameField.text.toString())

                val json = gson.toJson(newUser)

                prefsEdit.putString("userProfile", json)
                prefsEdit.apply()

                Log.i("ARPROJECT", "New user created")

                val intent = Intent(this@StartActivity, MainActivity::class.java)
                intent.putExtra("userProfile", newUser)
                startActivity(intent)
            }
        }
    }
}