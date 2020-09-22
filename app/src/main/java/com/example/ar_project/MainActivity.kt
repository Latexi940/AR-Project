package com.example.ar_project

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class MainActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment
    private var testRenderable: ModelRenderable? = null

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
            .setRegistryId("CesiumMan")
            .build()

        renderableFuture.thenAccept { it -> testRenderable = it }

        fragment.arSceneView.scene.addOnUpdateListener {
            frameUpdate()
        }
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
                    if (getSpawningChance(1000)) {
                        val anchor = hit!!.createAnchor()
                        val anchorNode = AnchorNode(anchor)
                        anchorNode.setParent(fragment.arSceneView.scene)
                        val mNode = TransformableNode(fragment.transformationSystem)
                        mNode.setParent(anchorNode)
                        mNode.renderable = testRenderable
                        mNode.setOnTapListener { hitTestRes: HitTestResult?, motionEv: MotionEvent? ->
                            Toast.makeText(
                                this,
                                "Catch this noob!",
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
        return if (randomNumber == 1) {
            Log.i("ARPROJECT", "Spawn!")
            true
        } else {
            false
        }
    }
}