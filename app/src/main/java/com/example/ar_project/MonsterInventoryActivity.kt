package com.example.ar_project

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_monster_inventory.*

class MonsterInventoryActivity : AppCompatActivity()  {
    private var user : User? = null
private var monstersList = user?.monsterCollection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monster_inventory)

    loadProfile()




      val amountOfMonsters =  user?.monsterCollection?.size

        tv_monsters.text = "Monsters collected: $amountOfMonsters"

        val monstersAdapter = monstersList?.let { MonstersAdapter(this, it) }

        lv_monsters.adapter = monstersAdapter


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