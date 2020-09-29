package com.example.ar_project

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat

class LocationService: Service(), LocationListener {

    var currentLocation: Location? = null
    var distanceTravelled = 0.0
    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.i("ARPROJECT", "Starting service")

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, this)

        return START_STICKY
    }

    override fun onLocationChanged(pos: Location) {
        Log.i("ARPROJECT", "New location")
        distanceTravelled += pos.distanceTo(pos)

        currentLocation = pos
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

}