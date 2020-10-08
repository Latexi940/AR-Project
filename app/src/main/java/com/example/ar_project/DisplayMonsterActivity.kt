package com.example.ar_project

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_display_monster.*

private lateinit var monsterFragment: ArFragment
private var testRenderable: ModelRenderable? = null

class DisplayMonsterActivity :  AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_monster)
        monsterFragment = supportFragmentManager.findFragmentById(R.id.arImage_fragment) as ArFragment

        spawn_btn.setOnClickListener{
            addObject()
        }





        var modelUri =
            Uri.parse("")

        val renderableFuture = ModelRenderable.builder()
            .setSource(
                this, RenderableSource.builder().setSource(
                    this, modelUri, RenderableSource.SourceType.GLTF2
                )
                    .setScale(1.3f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId("monster")
            .build()

        renderableFuture.thenAccept { it -> testRenderable = it }

        
    }

    private fun addObject() {
        val frame = monsterFragment.arSceneView.arFrame
        val point = getScreenCenter()
        val hits: List<HitResult>

        if (frame != null && testRenderable != null) {
            hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane) {
                    val anchor = hit!!.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(monsterFragment.arSceneView.scene)
                    val mNode = TransformableNode(monsterFragment.transformationSystem)
                    mNode.setParent(anchorNode)
                    mNode.renderable = testRenderable
                    mNode.select()

                    spawn_btn.visibility = (View.GONE)
                    break
                }
            }
        }
    }

    private fun getScreenCenter(): android.graphics.Point {
        val vw = findViewById<View>(android.R.id.content)
        return android.graphics.Point(vw.width / 2, vw.height / 2)
    }
}