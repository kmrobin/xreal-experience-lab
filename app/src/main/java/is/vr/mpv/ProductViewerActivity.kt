package `is`.vr.mpv

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import `is`.vr.mpv.databinding.ActivityProductViewerBinding

/**
 * Renders a 3D product with Google's <model-viewer> in a WebView. Assets are
 * served through WebViewAssetLoader over an https origin so local .glb files
 * load without file:// CORS issues. Touch orbit / pinch-zoom are built in.
 */
class ProductViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductViewerBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rawSrc = intent.getStringExtra(EXTRA_MODEL_SRC).orEmpty()
        // Local catalog entries are asset-relative (e.g. "models/x.glb"); remote
        // ones are full URLs. Serve local ones via the asset loader origin.
        val modelSrc = if (rawSrc.startsWith("http")) rawSrc else "$ASSET_BASE$rawSrc"
        binding.viewerTitle.text = intent.getStringExtra(EXTRA_TITLE).orEmpty()

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            mediaPlaybackRequiresUserGesture = false
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        binding.webView.setBackgroundColor(0xFF000000.toInt())
        binding.webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? = assetLoader.shouldInterceptRequest(request.url)

            override fun onPageFinished(view: WebView, url: String) {
                val escaped = modelSrc.replace("\\", "\\\\").replace("'", "\\'")
                view.evaluateJavascript("loadModel('$escaped');", null)
            }
        }
        binding.webView.loadUrl("${ASSET_BASE}model_viewer.html")

        binding.btnBack.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_MODEL_SRC = "model_src"
        const val EXTRA_TITLE = "title"
        private const val ASSET_BASE = "https://appassets.androidplatform.net/assets/"
    }
}
