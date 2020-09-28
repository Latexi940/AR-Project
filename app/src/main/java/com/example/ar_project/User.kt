package com.example.ar_project

import java.io.Serializable

class User(var name: String /*var password: String*/):Serializable {

    var distanceTravelled = 0.0

    var monsterCollection: MutableList<Monster> = java.util.ArrayList()

    override fun toString(): String {
        return "$name"
    }
}