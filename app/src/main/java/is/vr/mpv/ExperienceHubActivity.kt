package `is`.vr.mpv

import android.content.Intent
import android.os.Bundle
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
            Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show()
        }
        binding.tileShopping.root.setOnClickListener {
            Toast.makeText(this, R.string.coming_soon, Toast.LENGTH_SHORT).show()
        }
    }
}
