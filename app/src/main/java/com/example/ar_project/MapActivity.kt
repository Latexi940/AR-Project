package com.example.ar_project

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {


    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private var locationService = LocationService()
    private var serviceIsBound: Boolean = false
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.getService()
            serviceIsBound = true
        }

        override fun onServiceDisconnected(arg: ComponentName) {
            serviceIsBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mHandler = Handler()
        mRunnable = Runnable { updateMap()}
        mHandler.postDelayed(mRunnable, 1000)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.zoomTo(19)
    }

    override fun onStart() {
        super.onStart()

        Log.i("ARPROJECT", "Binding location service to MapActivity")
        Intent(this, LocationService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.i("ARPROJECT", "Unbinding location service from MapActivity")
        unbindService(connection)
        serviceIsBound = false
    }

    fun updateMap() {
        map.overlays.clear()

        val pos = locationService.currentLocation
        if(pos != null) {
            val geoPoint = GeoPoint(pos.latitude, pos.longitude)
            map.controller.setCenter(geoPoint)

            val marker = Marker(map)
            marker.position = geoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(marker)
        }

        mHandler.postDelayed(mRunnable, 1000)
    }

}