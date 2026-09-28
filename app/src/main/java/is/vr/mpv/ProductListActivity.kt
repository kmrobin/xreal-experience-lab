package `is`.vr.mpv

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import `is`.vr.mpv.databinding.ActivityExperienceListBinding

/** Product Reviewer: pick a 3D product, then inspect it by orbiting the model. */
class ProductListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExperienceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperienceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listEyebrow.text = getString(R.string.persona_product)
        binding.listTitle.text = getString(R.string.product_list_title)
        binding.listSubtitle.text = getString(R.string.product_list_subtitle)
        // Back button sits below the product tiles for this screen.
        binding.backButton.visibility = View.GONE
        binding.bottomBackButton.visibility = View.VISIBLE
        binding.bottomBackButton.setOnClickListener { finish() }

        binding.recycler.layoutManager = GridLayoutManager(this, 2)
        binding.recycler.adapter = ProductTileAdapter(ExperienceCatalog.products) { product ->
            openProduct(product)
        }
    }

    private fun openProduct(product: ProductItem) {
        val i = Intent(this, ProductViewerActivity::class.java)
        i.putExtra(ProductViewerActivity.EXTRA_MODEL_SRC, product.modelSrc)
        i.putExtra(ProductViewerActivity.EXTRA_TITLE, product.title)
        startActivity(i)
    }
}
