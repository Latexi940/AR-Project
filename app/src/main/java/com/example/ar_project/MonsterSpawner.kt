package com.example.ar_project

import android.content.Context
import android.location.Location
import android.net.Uri
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable

class MonsterSpawner(private val context: Context) {

    val monsterNames = listOf("Avocado", "Cesium", "Ballero")


    fun createMonster(pos: Location?): Monster {
        /*val rnd = (0..2).random()*/

        /*var monsterName = monsterNames[rnd]*/

        val monsterName = "Avocado"

        return Monster(monsterName, pos)
    }

    fun getModel(): ModelRenderable? {
        var testRenderable: ModelRenderable? = null
        val modelUri =
            Uri.parse("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Avocado/glTF/Avocado.gltf")

        val renderableFuture = ModelRenderable.builder()
            .setSource(
                context, RenderableSource.builder().setSource(
                    context, modelUri, RenderableSource.SourceType.GLTF2
                )
                    .setScale(2.0f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId("Avocado")
            .build()

        renderableFuture.thenAccept { it -> testRenderable = it }

        return testRenderable
    }
}