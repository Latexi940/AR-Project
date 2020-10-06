package com.example.ar_project

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var user: User? = null

    private lateinit var fragment: ArFragment
    private var locationService = LocationService()
    private var serviceIsBound: Boolean = false
    private var testRenderable: ModelRenderable? = null
    var userLocation: Location? = null
    private var spawningLocation: Location? = null

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
        setContentView(R.layout.activity_main)

        user = intent.getSerializableExtra("userProfile") as User

        Log.i("ARPROJECT", "User ${user?.name} loaded")

        fragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
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

        val intent = Intent(applicationContext, LocationService::class.java)
        startService(intent)

        profile_btn.setOnClickListener(){
            openProfile()
        }
    }

    override fun onStart() {
        super.onStart()

        Log.i("ARPROJECT", "Binding location service to MainActivity")
        Intent(this, LocationService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun createMonsterToView(): Monster {
        val spawner = MonsterSpawner(this)
        val monsterToSpawn = spawner.createMonster(userLocation)
        testRenderable = monsterToSpawn.model

        Log.i("ARPROJECT", "Monster spawned: ${monsterToSpawn.name}")

        return monsterToSpawn
    }

    private fun frameUpdate() {
        userLocation = locationService.currentLocation
        val frame = fragment.arSceneView.arFrame
        val point = getScreenCenter()
        val hits: List<HitResult>

        if (frame != null && userLocation != null) {
            hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane) {
                    if (getSpawningChance(100) && isSpawningAllowed()) {
                        val monster = createMonsterToView()
                        if (testRenderable != null) {
                            Log.i("ARPROJECT", "Model rendering")
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
                                    "${monster.name} caught!",
                                    Toast.LENGTH_SHORT
                                ).show()

                              //  user?.monsterCollection?.add(monster)
                                mNode.setParent(null)
                            }
                            break
                        }
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

    private fun isSpawningAllowed(): Boolean {
        if (spawningLocation == null) {
            return true
        }
        (return userLocation?.distanceTo(spawningLocation)!! > 50)
    }

    override fun onPause() {
        super.onPause()

        val prefs: SharedPreferences = getPreferences(MODE_PRIVATE)
        val prefsEdit: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()

        val newUser = user
        val json = gson.toJson(newUser)

        prefsEdit.putString("userProfile", json)
        prefsEdit.apply()

        Log.i("ARPROJECT", "Saving profile...")
    }

    override fun onStop() {
        super.onStop()
        user?.distanceTravelled = locationService.distanceTravelled
        Log.i("ARPROJECT", "Unbinding location service from MainActivity")
        unbindService(connection)
        serviceIsBound = false
    }


    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(applicationContext, LocationService::class.java)
        stopService(intent)
    }

    fun openProfile(){
        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
        startActivity(intent)
    }
}