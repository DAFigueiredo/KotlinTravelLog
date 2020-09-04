package com.example.kotlintravellog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val placesArray = ArrayList<Place>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val database = openOrCreateDatabase("Places", Context.MODE_PRIVATE, null)
            var cursor = database.rawQuery("SELECT * FROM places", null)

            val nameIndex = cursor.getColumnIndex("name")
            val latIndex = cursor.getColumnIndex("latitude")
            val longIndex = cursor.getColumnIndex("longitude")

            while (cursor.moveToNext()) {

                val nameFromDatabase = cursor.getString(nameIndex)
                val latFromDatabase = cursor.getDouble(latIndex)
                val longFromDatabase = cursor.getDouble(longIndex)

                val myPlace = Place(nameFromDatabase, latFromDatabase, longFromDatabase)

                placesArray.add(myPlace)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

        val customAdapter = LocationsAdapter(placesArray, this)
        listView.adapter = customAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            intent.putExtra("info", "old")
            intent.putExtra("selectedPlace", placesArray.get(position))
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_place, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.add_place) {
            val intent = Intent(applicationContext, MapsActivity::class.java)
            intent.putExtra("info", "new")
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }


}