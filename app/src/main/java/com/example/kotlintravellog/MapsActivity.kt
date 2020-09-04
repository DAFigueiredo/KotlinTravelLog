package com.example.kotlintravellog

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null
    private lateinit var mMap: GoogleMap

    val location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intentToMain = Intent(this, MainActivity::class.java)
        startActivity(intentToMain)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(myListener)


        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {

            override fun onLocationChanged(p0: Location) {

                if (p0 != null) {
                    mMap.clear()

                    var userLocation = LatLng(p0!!.latitude, p0!!.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Current location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17f))

                    val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())

                    try {
                        val visitedList =
                            geocoder.getFromLocation(location?.latitude!!, location.longitude, 1)

                        if (visitedList != null && visitedList.size > 0) {

                            println(visitedList.get(0).toString())

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }


            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onProviderDisabled(provider: String) {
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1,
                1f,
                locationListener as LocationListener
            )

            val intent = intent
            val info = intent.getStringExtra("info")

            if (info.equals("new")) {

                mMap.clear()
                val lastLocation =
                    locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                var lastUserLocation = LatLng(lastLocation!!.latitude, lastLocation.longitude)
                mMap.addMarker(MarkerOptions().position(lastUserLocation).title("Current location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 17f))


            } else {
                mMap.clear()
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)
                val name = intent.getStringExtra("name")
                val location = LatLng(latitude, longitude)

                mMap.addMarker(MarkerOptions().position(location).title(name))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))

            }

            val lastLocation = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (lastLocation != null) {
                val lastKnownLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                mMap.addMarker(MarkerOptions().position(lastKnownLatLng).title("Current location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLatLng, 17f))
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == 1) {
            if (grantResults.isNotEmpty()) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager?.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        2,
                        2f,
                        locationListener!!
                    )
                }
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val myListener = object : GoogleMap.OnMapLongClickListener {

        override fun onMapLongClick(p0: LatLng?) {

            mMap.clear()

            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())

            if (p0 != null) {
                var address = ""

                try {

                    val visitedList = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)
                    if (visitedList != null && visitedList.size > 0) {

                        if (visitedList[0].thoroughfare != null) {
                            address += visitedList[0].thoroughfare
                            if (visitedList[0].subThoroughfare != null) {
                                address += ", ${visitedList[0].subThoroughfare}"
                            }
                        }

                    } else {
                        address = "New Place"
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }


                mMap.addMarker(MarkerOptions().position(p0).title(address))

                Toast.makeText(applicationContext, "New place created: $address", Toast.LENGTH_LONG)
                    .show()
                val newPlace = Place(address,p0.latitude,p0.longitude)

                try {
                    val database = openOrCreateDatabase("Places", Context.MODE_PRIVATE, null)

                    database.execSQL("CREATE TABLE IF NOT EXISTS places (name VARCHAR, latitude DOUBLE, longitude DOUBLE)")

                    val toCompilde =
                        "INSERT INTO places (name, latitude, longitude) VALUES (?, ?, ?)"
                    val sqLiteStatement = database.compileStatement(toCompilde)

                    sqLiteStatement.bindString(1, newPlace.address)
                    sqLiteStatement.bindDouble(2, newPlace.latitude!!)
                    sqLiteStatement.bindDouble(3, newPlace.longitude!!)

                    sqLiteStatement.execute()


                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else {
                Toast.makeText(applicationContext, "Try again", Toast.LENGTH_LONG).show()
            }
        }

    }

}