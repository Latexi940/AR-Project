package com.example.ar_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class MonstersAdapter(context: Context, private val monsters: MutableList<Monster>): BaseAdapter() {
   // private var monsterName : String? = null
    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return monsters.size
    }

    override fun getItem(position: Int): Any {
        return monsters[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.monster_list_item, parent, false)

        val thisMonster = monsters[position]

        var tv = rowView.findViewById(R.id.tv_name) as TextView
        tv.text = thisMonster.name
       // monsterName = thisMonster.name
/*
        tv = rowView.findViewById(R.id.tvStartDuty) as TextView
        tv.text = Integer.toString(thisMonster.startDuty)

        tv = rowView.findViewById(R.id.tvEndDuty) as TextView
        tv.text = Integer.toString(thisMonster.endDuty)
*/
        return rowView
    }
}