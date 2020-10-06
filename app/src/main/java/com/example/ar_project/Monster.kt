package com.example.ar_project

import android.location.Location
import com.google.ar.sceneform.rendering.ModelRenderable

class Monster(val name: String, val location: Location?) {
    var xp = 0
    var lvl = 1


    fun gainXP(newXp:Int){
        xp += newXp

        if(xp >= 10){
            xp - 10
            lvl + 1
        }
    }

    fun getCatchAssist():Double{
        var catchAssist = 0.0
        if(lvl > 1){
            catchAssist += lvl / 20
        }
        return catchAssist
    }
}