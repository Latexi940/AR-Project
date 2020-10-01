package com.example.ar_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_monster_inventory.*

class MonsterInventoryActivity : AppCompatActivity()  {
private val monstersList = user?.monsterCollection
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monster_inventory)


      val amountOfMonsters =  user?.monsterCollection?.size

        tv_monsters.text = "Monsters collected: $amountOfMonsters"

       // val monstersAdapter = monstersList?.let { MonstersAdapter(this, it) }

       // lv_monsters.adapter = monstersAdapter


    }

}