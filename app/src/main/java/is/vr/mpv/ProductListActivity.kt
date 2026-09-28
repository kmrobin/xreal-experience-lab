package `is`.vr.mpv

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import `is`.vr.mpv.databinding.ActivityExperienceListBinding
import java.io.File

/** Product Reviewer: pick a 3D product, then inspect it by orbiting the model. */
class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExperienceListBinding

    private val pickSceneImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { openPickedScene(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperienceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shopping = intent.getBooleanExtra(EXTRA_SHOPPING, false)
        if (shopping) {
            binding.listEyebrow.text = getString(R.string.persona_shopping)
            binding.listTitle.text = getString(R.string.shopping_list_title)
            binding.listSubtitle.text = getString(R.string.shopping_list_subtitle)
            binding.browseGalleryButton.visibility = View.VISIBLE
            binding.browseGalleryButton.setOnClickListener { pickSceneImage.launch("image/*") }
        } else {
            binding.listEyebrow.text = getString(R.string.persona_product)
            binding.listTitle.text = getString(R.string.product_list_title)
            binding.listSubtitle.text = getString(R.string.product_list_subtitle)
        }
        // Back button sits below the product tiles for this screen.
        binding.backButton.visibility = View.GONE
        binding.bottomBackButton.visibility = View.VISIBLE
        binding.bottomBackButton.setOnClickListener { finish() }

        val products =
            if (shopping) ExperienceCatalog.products.filter { it.shopping }
            else ExperienceCatalog.products
        binding.recycler.layoutManager = GridLayoutManager(this, 2)
        binding.recycler.adapter = ProductTileAdapter(products) { product ->
            openProduct(product)
        }
    }

    private fun openProduct(product: ProductItem) {
        val i = Intent(this, ProductViewerActivity::class.java)
        i.putExtra(ProductViewerActivity.EXTRA_MODEL_SRC, product.modelSrc)
        i.putExtra(ProductViewerActivity.EXTRA_TITLE, product.title)
        startActivity(i)
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
            Log.e("ProductList", "Failed to open picked scene", e)
            Toast.makeText(this, R.string.scene_open_failed, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_SHOPPING = "shopping"
    }
}
