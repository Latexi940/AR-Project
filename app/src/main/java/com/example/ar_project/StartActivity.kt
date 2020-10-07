package com.example.ar_project

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_start.*


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        }

        val prefs: SharedPreferences = getSharedPreferences("PROFILE", MODE_PRIVATE)
        val prefsEdit: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()

        //Uncomment this to clear user data
        //prefsEdit.clear().apply()

        val retrivedJson = prefs.getString("userProfile", "no user")


        if (retrivedJson != "no user") {
            val userProfile = gson.fromJson(retrivedJson, User::class.java)
            Log.i("ARPROJECT", "User profile found: $userProfile")

            val intent = Intent(this@StartActivity, MainActivity::class.java)
            //intent.putExtra("userProfile", userProfile)
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

                prefsEdit.putString("userProfile", json).commit()

                Log.i("ARPROJECT", "New user created")

                val intent = Intent(this@StartActivity, MainActivity::class.java)
                intent.putExtra("userProfile", newUser)
                startActivity(intent)

            }
        }
    }
}