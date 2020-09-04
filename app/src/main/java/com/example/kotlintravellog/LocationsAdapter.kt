package com.example.kotlintravellog

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.location_row.view.*

class LocationsAdapter(private val placeList: ArrayList<Place>, private val context: Activity) :
    ArrayAdapter<Place>(
        context,
        R.layout.location_row, placeList
    ) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater = context.layoutInflater
        val customView = layoutInflater.inflate(R.layout.location_row, null, true)

        customView.firstEntry.text = placeList.get(position).address

        return customView
    }

}