package com.example.ar_project

import android.content.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.sqrt


class MainActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment
    private var locationService = LocationService()
    private var serviceIsBound: Boolean = false
    private var testRenderable: ModelRenderable? = null
    var userLocation: Location? = null
    private var spawningLocation: Location? = null
    private var user: User? = null
    private var mSensorManager: SensorManager? = null
    private var mAccel = 0f
    private var mAccelCurrent = 0f
    private var mAccelLast = 0f
    private var catchingInProgress = false
    private var shakeDetected = false
    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

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

        loadProfile()
        Toast.makeText(this, "Happy catching ${user}!", Toast.LENGTH_SHORT).show()

        fragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        fragment.arSceneView.scene.addOnUpdateListener {
            frameUpdate()
        }

        val intent = Intent(applicationContext, LocationService::class.java)
        startService(intent)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(mSensorManager)!!.registerListener(
            mSensorListener, mSensorManager!!.getDefaultSensor(
                Sensor.TYPE_ACCELEROMETER
            ),
            SensorManager.SENSOR_DELAY_NORMAL
        );
        mAccel = 10f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        map_btn.setOnClickListener {
            switchToMap()
        }
    }

    private val mSensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            mAccelLast = mAccelCurrent
            mAccelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta = mAccelCurrent - mAccelLast
            mAccel = mAccel * 0.9f + delta
            if (mAccel > 12) {
                if(catchingInProgress){
                    Log.i("ARPROJECT", "Shake detected")
                    shakeDetected = true
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
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
        testRenderable = spawner.getModel()

        return monsterToSpawn
    }

    private fun frameUpdate() {
        userLocation = locationService.currentLocation
        val frame = fragment.arSceneView.arFrame
        val point = getScreenCenter()
        val hits: List<HitResult>

        if (frame != null && userLocation != null && user != null) {
            hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane) {
                    if (getSpawningChance(10) && isSpawningAllowed()) {
                        val monster = createMonsterToView()
                        if (testRenderable != null) {
                            Log.i("ARPROJECT", "${monster.name} rendering")
                            spawningLocation = userLocation
                            val anchor = hit!!.createAnchor()
                            val anchorNode = AnchorNode(anchor)
                            anchorNode.setParent(fragment.arSceneView.scene)
                            val mNode = TransformableNode(fragment.transformationSystem)
                            mNode.setParent(anchorNode)
                            mNode.renderable = testRenderable
                            mNode.setOnTapListener { hitTestRes: HitTestResult?, motionEv: MotionEvent? ->
                                Log.i("ARPROJECT", "Model tapped")
                                catchMonster(monster, mNode)
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

    override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(
            mSensorListener, mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    override fun onPause() {
        super.onPause()
        mSensorManager!!.unregisterListener(mSensorListener);
        saveProfile()
    }

    override fun onStop() {
        super.onStop()
        Log.i("ARPROJECT", "Unbinding location service from MainActivity")
        unbindService(connection)
        serviceIsBound = false
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(applicationContext, LocationService::class.java)
        stopService(intent)
    }

    private fun switchToMap() {
        val intent = Intent(this@MainActivity, MapActivity::class.java)
        startActivity(intent)
    }

    private fun saveProfile() {
        if (user != null) {
            user!!.distanceTravelled += locationService.distanceTravelled
            locationService.distanceTravelled = 0.0

            val prefs: SharedPreferences = getSharedPreferences("PROFILE", MODE_PRIVATE)
            val prefsEdit: SharedPreferences.Editor = prefs.edit()
            val gson = Gson()

            val jsonUser = gson.toJson(user)
            prefsEdit.putString("userProfile", jsonUser).apply()

            Log.i("ARPROJECT", "Profile saved")
        } else {
            Log.i("ARPROJECT", "No user. Cannot save.")
        }
    }

    private fun loadProfile() {
        val prefs: SharedPreferences = getSharedPreferences("PROFILE", MODE_PRIVATE)
        val gson = Gson()

        val jsonUser = prefs.getString("userProfile", "no user")

        if (jsonUser != "no user") {
            user = gson.fromJson(jsonUser, User::class.java)
            Log.i("ARPROJECT", "User profile loaded to Main: $user.")
        } else {
            Log.i("ARPROJECT", "No user in SharedPreferences")
            val i = Intent()
            user = i.getSerializableExtra("userProfile") as User?
            if (user != null) {
                Log.i("ARPROJECT", "$user loaded with intent")
            }
        }
    }

    private fun catchMonster(monster: Monster, node: TransformableNode) {
        catchingInProgress = true
        shake_text.visibility = View.VISIBLE

        mHandler = Handler()
        mRunnable = Runnable { detectShake(monster, node) }
        mHandler.postDelayed(mRunnable, 200)

    }

    private fun detectShake(monster:Monster, node:TransformableNode) {
        if(shakeDetected){
            Toast.makeText(
                this,
                "${monster.name} caught!",
                Toast.LENGTH_SHORT
            ).show()
            user?.monsterCollection?.add(monster)
            node.setParent(null)
            Log.i(
                "ARPROJECT",
                "Collection size is now: ${user?.monsterCollection?.size}"
            )
            shake_text.visibility = View.GONE
            catchingInProgress = false
            shakeDetected = false
        }else{
            mHandler.postDelayed(mRunnable, 200)
        }
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}