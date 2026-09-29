package `is`.vr.mpv

import android.annotation.SuppressLint
import android.content.Intent
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
    private var products: List<ProductItem> = emptyList()
    private var currentIndex = -1
    private var pendingSrc = ""   // model to (re)load once the page is ready

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigate within the same list the user came from (Shopping or all).
        val shopping = intent.getBooleanExtra(EXTRA_SHOPPING, false)
        val productId = intent.getStringExtra(EXTRA_PRODUCT_ID)
        products = if (shopping) ExperienceCatalog.products.filter { it.shopping }
        else ExperienceCatalog.products
        currentIndex = products.indexOfFirst { it.id == productId }

        val hasNav = currentIndex >= 0 && products.size > 1
        if (currentIndex >= 0) {
            binding.viewerTitle.text = products[currentIndex].title
            pendingSrc = resolveSrc(products[currentIndex].modelSrc)
        } else {
            // Direct launch (e.g. deep link): show just the provided model.
            binding.viewerTitle.text = intent.getStringExtra(EXTRA_TITLE).orEmpty()
            pendingSrc = resolveSrc(intent.getStringExtra(EXTRA_MODEL_SRC).orEmpty())
        }
        binding.btnPrevProduct.visibility = if (hasNav) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnNextProduct.visibility = if (hasNav) android.view.View.VISIBLE else android.view.View.GONE

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
                loadCurrent()
            }
        }
        binding.webView.loadUrl("${ASSET_BASE}model_viewer.html")

        binding.btnExitProduct.setOnClickListener { goHome() }
        binding.btnPrevProduct.setOnClickListener { showProduct(currentIndex - 1) }
        binding.btnNextProduct.setOnClickListener { showProduct(currentIndex + 1) }
    }

    // Exits straight to the main hub, clearing the list screens behind it.
    private fun goHome() {
        startActivity(
            Intent(this, ExperienceHubActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
        finish()
    }

    private fun resolveSrc(raw: String): String =
        if (raw.startsWith("http")) raw else "$ASSET_BASE$raw"

    private fun loadCurrent() {
        val escaped = pendingSrc.replace("\\", "\\\\").replace("'", "\\'")
        binding.webView.evaluateJavascript("loadModel('$escaped');", null)
    }

    // Swaps the model in place (no WebView reload) for the next/previous product.
    private fun showProduct(index: Int) {
        if (products.isEmpty()) return
        currentIndex = (index % products.size + products.size) % products.size
        val p = products[currentIndex]
        binding.viewerTitle.text = p.title
        pendingSrc = resolveSrc(p.modelSrc)
        loadCurrent()
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_MODEL_SRC = "model_src"
        const val EXTRA_TITLE = "title"
        const val EXTRA_PRODUCT_ID = "product_id"
        const val EXTRA_SHOPPING = "shopping"
        private const val ASSET_BASE = "https://appassets.androidplatform.net/assets/"
    }
}
