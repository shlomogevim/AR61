package com.sg.ar61

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var arFragment: ArFragment
    private val modelResourcesIds= arrayOf(
        R.raw.star_destroyer,
        R.raw.tie_silencer,
        R.raw.xwing
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFragment=fragment as ArFragment
        arFragment.setOnTapArPlaneListener { hitResult,_, _ ->
            val randoId =modelResourcesIds.random()
            loadModelAndAddToScene(hitResult.createAnchor(),randoId)
        }
    }

    private fun loadModelAndAddToScene(anchor: Anchor, modelResourceId:Int){

        ModelRenderable.builder()
            .setSource(this,modelResourceId)
            .build()
            .thenAccept { modelReanderable ->
                val spaceship = when (modelResourceId){
                    R.raw.star_destroyer -> Spaceship.StarDestroyer
                    R.raw.tie_silencer -> Spaceship.TieSilencer
                    R.raw.xwing -> Spaceship.XWing
                    else->Spaceship.XWing
                }
                addNodeToSence(anchor,modelReanderable,spaceship)
            }.exceptionally {
                Toast.makeText(this,"Something go wrong with nodes:$it", Toast.LENGTH_LONG).show()
                null
            }

    }


    private fun addNodeToSence(anchore: Anchor, modelRenderable: ModelRenderable, spaceship: Spaceship){
        val anchorNode = AnchorNode(anchore)
        val rotatingNode=RotatingNode(spaceship.degreesPerSecond).apply {
            setParent(anchorNode)
        }
        Node().apply {
            renderable=modelRenderable
            setParent(rotatingNode)
            localPosition= Vector3(spaceship.radius,spaceship.height,0f)
            localRotation= Quaternion.eulerAngles(Vector3(0f,spaceship.rotationDegrees,0f))
        }

        arFragment.arSceneView.scene.addChild(anchorNode)

    }
}