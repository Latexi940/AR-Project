package com.example.ar_project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        monsters_btn.setOnClickListener() {
             val intent = Intent(this, MonsterInventoryActivity::class.java)
            startActivity(intent)
        }
    }
}