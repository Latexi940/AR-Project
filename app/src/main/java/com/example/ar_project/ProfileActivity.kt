package com.example.ar_project

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

       // user= intent.getSerializableExtra("User") as User;
        loadProfile()


         tv_name.text = user!!.name
       monsters_btn.setOnClickListener() {
             val intent = Intent(this, MonsterInventoryActivity::class.java)
          // intent.putExtra("User", user);

           startActivity(intent)
        }
    }
    private fun loadProfile() {
        val prefs: SharedPreferences = getSharedPreferences("PROFILE", MODE_PRIVATE)
        val gson = Gson()

        val jsonUser = prefs.getString("userProfile", "no user")

        if (jsonUser != "no user") {
            user = gson.fromJson(jsonUser, User::class.java)
            Log.i("ARPROJECT", "User profile loaded to Main: $user.")
        } else {
            Log.i("ARPROJECT", "No user in SharedPreferences")
            val i = Intent()
            user = i.getSerializableExtra("userProfile") as User?
            if(user != null){
                Log.i("ARPROJECT", "$user loaded with intent")
            }
        }
    }
}