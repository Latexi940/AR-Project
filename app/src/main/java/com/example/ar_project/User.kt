package com.example.ar_project


class User(var name: String /*var password: String*/) {

    var distanceTravelled = 0.0

    var monsterCollection: MutableList<Monster> = java.util.ArrayList()

    override fun toString(): String {
        return name
    }
}