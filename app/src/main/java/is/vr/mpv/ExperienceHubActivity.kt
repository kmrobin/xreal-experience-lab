package `is`.vr.mpv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import `is`.vr.mpv.databinding.ActivityExperienceHubBinding

/** Landing screen: pick a persona (Product Reviewer or Scene Reviewer). */
class ExperienceHubActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExperienceHubBinding

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
            startActivity(
                Intent(this, ProductListActivity::class.java)
                    .putExtra(ProductListActivity.EXTRA_SHOPPING, true)
            )
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

    private companion object {
        const val TAG = "ExperienceHub"
    }
}
