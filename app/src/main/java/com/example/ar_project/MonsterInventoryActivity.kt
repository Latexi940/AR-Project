package com.example.ar_project

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_monster_inventory.*

class MonsterInventoryActivity : AppCompatActivity()  {
    private var user : User? = null
    private var monsterName : String? = null
private var monstersList : MutableList<Monster>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monster_inventory)

    loadProfile()

        monstersList = user?.monsterCollection


      val amountOfMonsters =  user?.monsterCollection?.size

        tv_monsters.text = "Monsters collected: $amountOfMonsters"

        tv_monsters.setOnClickListener{
            switchToMonsterView()

        }

        val monstersAdapter = monstersList?.let { MonstersAdapter(this, it) }

        lv_monsters.adapter = monstersAdapter

        lv_monsters.setOnItemClickListener { parent, view, position, id ->

            monsterName = this.monsterName


            switchToMonsterView()

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

        }
    }
    private fun switchToMonsterView() {
        val intent = Intent(this, DisplayMonsterActivity::class.java)
        intent.putExtra("monsterName",monsterName)
        startActivity(intent)
    }
}