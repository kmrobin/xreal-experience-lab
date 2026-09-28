package `is`.vr.mpv

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

/**
 * Parses custom-scheme deep links (experiencelab://…) and maps them onto the
 * app's existing screens. Returns which action the hub should take; never
 * creates a parallel navigation system.
 */
object DeepLinkRouter {

    const val SCHEME = "experiencelab"
    private const val TAG = "DeepLinkRouter"

    /** What the hub should do for an incoming link. */
    data class Result(
        val intent: Intent?,      // screen to open, or null to stay on the hub
        val toastRes: Int?,       // optional user message
        val valid: Boolean,
    )

    fun route(context: Context, uri: Uri): Result {
        Log.d(TAG, "Incoming deep link: $uri")
        // Host-less URIs (scheme://path) expose the first segment as the host.
        val host = (uri.host ?: "").lowercase()
        val id = uri.pathSegments.firstOrNull()
        val query = uri.queryParameterNames.associateWith { uri.getQueryParameter(it) }
        Log.d(TAG, "Parsed host='$host' id='$id' query=$query")

        val result = when (host) {
            "", "home" -> Result(null, null, true)
            "product" -> Result(productIntent(context, id), null, true)
            "scene" -> Result(sceneIntent(context, id), null, true)
            "tourist", "shopping" -> Result(null, R.string.coming_soon, true)
            else -> {
                Log.w(TAG, "Unsupported deep link host: '$host'")
                Result(null, R.string.deeplink_invalid, false)
            }
        }
        Log.d(TAG, "Destination=${result.intent?.component?.shortClassName ?: "hub"} valid=${result.valid}")
        return result
    }

    private fun productIntent(context: Context, id: String?): Intent {
        if (id != null) {
            val product = ExperienceCatalog.products.firstOrNull { it.id == id }
            if (product != null) {
                Log.d(TAG, "Opening product '${product.id}'")
                return Intent(context, ProductViewerActivity::class.java).apply {
                    putExtra(ProductViewerActivity.EXTRA_MODEL_SRC, product.modelSrc)
                    putExtra(ProductViewerActivity.EXTRA_TITLE, product.title)
                }
            }
            Log.w(TAG, "Unknown product id '$id' -> product list")
        }
        return Intent(context, ProductListActivity::class.java)
    }

    private fun sceneIntent(context: Context, id: String?): Intent {
        val intent = Intent(context, SceneListActivity::class.java)
        if (id != null) {
            intent.putExtra(SceneListActivity.EXTRA_OPEN_SCENE_ID, id)
            Log.d(TAG, "Scene list requested to open scene id '$id'")
        }
        return intent
    }
}
