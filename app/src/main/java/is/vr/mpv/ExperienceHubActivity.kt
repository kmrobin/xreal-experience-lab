package `is`.vr.mpv

import android.content.Intent
import android.os.Bundle
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
    }
}
