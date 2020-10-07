package com.example.ar_project

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private var locationService = LocationService()
    private var lastKnownLocation: GeoPoint? = null
    private var serviceIsBound: Boolean = false
    private var mapIsUpdating: Boolean = false
    private var locationIsSet: Boolean = false
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

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.zoomTo(19.5)
        map.minZoomLevel = 19.5
        map.maxZoomLevel = 19.5
        map.setBuiltInZoomControls(false)
        mapIsUpdating = true

        mHandler = Handler()
        mRunnable = Runnable { updateMap() }
        mHandler.postDelayed(mRunnable, 500)

        center_map_btn.setOnClickListener{
            if(lastKnownLocation != null){
                map.controller.setCenter(lastKnownLocation)
            }
        }

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

    private fun updateMap() {
        map.overlays.clear()

        val pos = locationService.currentLocation
        if (pos != null) {
            val geoPoint = GeoPoint(pos.latitude, pos.longitude)
            lastKnownLocation = geoPoint
            if(!locationIsSet) {
                locationText.visibility = View.GONE
                Log.i("ARPROJECT", "Location set")
                locationIsSet = true
                map.controller.setCenter(geoPoint)
            }
            val marker = Marker(map)
            val icon = resources.getDrawable(R.drawable.map_icon)
            marker.icon = icon
            marker.position = geoPoint
            marker.setInfoWindow(null)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            map.overlays.add(marker)
        }
        if (mapIsUpdating) {
            mHandler.postDelayed(mRunnable, 500)
        }
    }

    override fun onBackPressed() {
        mapIsUpdating = false
        finish()
    }
}
