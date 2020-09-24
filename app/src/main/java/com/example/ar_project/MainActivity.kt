package com.example.ar_project

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var fragment: ArFragment
    private var testRenderable: ModelRenderable? = null
    private var userLocation: Location? = null
    private var spawningLocation: Location? = null
    private var distanceTravelled = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        //Avocado use scale 1.3
        val modelUri =
            Uri.parse("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf")

        val renderableFuture = ModelRenderable.builder()
            .setSource(
                this, RenderableSource.builder().setSource(
                    this, modelUri, RenderableSource.SourceType.GLTF2
                )
                    .setScale(1.3f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId("Avocado")
            .build()

        renderableFuture.thenAccept { it -> testRenderable = it }

        fragment.arSceneView.scene.addOnUpdateListener {
            frameUpdate()
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
        }

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, this)
    }

    private fun frameUpdate() {
        val frame = fragment.arSceneView.arFrame
        val point = getScreenCenter()
        val hits: List<HitResult>

        if (frame != null && testRenderable != null) {
            hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane) {
                    if (getSpawningChance(100) && isSpawningAllowed()) {
                        Log.i("ARPROJECT", "Spawn!")
                        spawningLocation = userLocation
                        val anchor = hit!!.createAnchor()
                        val anchorNode = AnchorNode(anchor)
                        anchorNode.setParent(fragment.arSceneView.scene)
                        val mNode = TransformableNode(fragment.transformationSystem)
                        mNode.setParent(anchorNode)
                        mNode.renderable = testRenderable
                        mNode.setOnTapListener { hitTestRes: HitTestResult?, motionEv: MotionEvent? ->
                            Toast.makeText(
                                this,
                                "Catch this, noob!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        break
                    }
                }
            }
        }
    }

    private fun getScreenCenter(): android.graphics.Point {
        val vw = findViewById<View>(android.R.id.content)
        return android.graphics.Point(vw.width / 2, vw.height / 2)
    }

    private fun getSpawningChance(chance: Int): Boolean {
        val randomNumber = (0..chance).random()
        return randomNumber == 1
    }

    private fun isSpawningAllowed(): Boolean{
        if(spawningLocation == null){
            return true
        }
        (return userLocation?.distanceTo(spawningLocation)!! > 50)
    }

    override fun onLocationChanged(currentLocation: Location) {

        if(userLocation != null){
            distanceTravelled += currentLocation.distanceTo(userLocation)
        }

        userLocation = currentLocation
    }

    override fun onStatusChanged(p: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p: String?) {
    }

    override fun onProviderDisabled(p: String?) {
    }
}