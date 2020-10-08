package com.example.ar_project

import android.content.Context
import android.location.Location
import android.net.Uri
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable

class MonsterSpawner(private val context: Context) {

    val monsterNames = listOf("longcatto", "ugly")
    private var monsterName: String? = null
    private var modelUri : Uri? = null

    fun createMonster(pos: Location?): Monster {
        val rnd = (0..1).random()

         monsterName = monsterNames[rnd]

      //  val monsterName = "Avocado"

        return Monster(monsterName!!, pos)
    }

    fun getModel(): ModelRenderable? {
        var testRenderable: ModelRenderable? = null

        if (monsterName == "longcatto"){
            modelUri = Uri.parse("longcatto.gltf")
        } else if(monsterName == "ugly") {
            modelUri = Uri.parse("monster1.gltf")
        }



        val renderableFuture = ModelRenderable.builder()
            .setSource(
                context, RenderableSource.builder().setSource(
                    context, modelUri, RenderableSource.SourceType.GLTF2
                )
                    .setScale(0.05f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId("Avocado")
            .build()

        renderableFuture.thenAccept { it -> testRenderable = it }

        return testRenderable
    }
}