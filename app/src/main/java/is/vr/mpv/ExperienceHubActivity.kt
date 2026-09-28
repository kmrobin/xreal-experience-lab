package `is`.vr.mpv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import `is`.vr.mpv.databinding.ActivityExperienceHubBinding
import java.io.File

/** Landing screen: pick a persona (Product Reviewer or Scene Reviewer). */
class ExperienceHubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExperienceHubBinding

    private val pickSceneImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { openPickedScene(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperienceHubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tileProduct.root.setOnClickListener {
            startActivity(Intent(this, ProductListActivity::class.java))
        }
        binding.tileScene.root.setOnClickListener {
            startActivity(Intent(this, SceneListActivity::class.java))
        }
        binding.tileTourist.root.setOnClickListener {
            startActivity(
                Intent(this, SceneListActivity::class.java)
                    .putExtra(SceneListActivity.EXTRA_CATEGORY, "tourist")
            )
        }
        binding.tileShopping.root.setOnClickListener {
            pickSceneImage.launch("image/*")
        }

        // Cold start via a deep link.
        handleDeepLink(intent)
    }

    // Deep link while the hub is already running / in the background (singleTask).
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri = intent?.data ?: return
        if (intent.action != Intent.ACTION_VIEW) return

        val result = DeepLinkRouter.route(this, uri)
        result.toastRes?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        result.intent?.let {
            try {
                startActivity(it)
                Log.d(TAG, "Navigated to ${it.component?.shortClassName}")
            } catch (e: Exception) {
                Log.e(TAG, "Deep-link navigation failed for $uri", e)
            }
        }
    }

    // Copies a gallery-picked image into cache and opens it as a 360 scene.
    private fun openPickedScene(uri: Uri) {
        try {
            val file = File(cacheDir, "picked_scene.img")
            contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            } ?: throw IllegalStateException("Could not open picked image")
            startActivity(
                Intent(this, MPVActivity::class.java)
                    .putExtra("filepath", file.absolutePath)
                    .putExtra("show_exit", true)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open picked scene", e)
            Toast.makeText(this, R.string.scene_open_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private companion object {
        const val TAG = "ExperienceHub"
    }
}
