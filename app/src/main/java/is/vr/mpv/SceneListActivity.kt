package `is`.vr.mpv

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import `is`.vr.mpv.databinding.ActivityExperienceListBinding
import java.io.File

/** Scene Reviewer: pick a 360° panorama, then explore it with head rotation. */
class SceneListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExperienceListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperienceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.listEyebrow.text = getString(R.string.persona_scene)
        binding.listTitle.text = getString(R.string.scene_list_title)
        binding.listSubtitle.text = getString(R.string.scene_list_subtitle)
        binding.backButton.setOnClickListener { finish() }

        val scenes = ExperienceCatalog.scenes
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = ExperienceCardAdapter(
            this,
            scenes.map { CardEntry(it.title, it.subtitle, it.thumbAsset) },
        ) { pos -> openScene(scenes[pos]) }
    }

    private fun openScene(scene: SceneItem) {
        val file = copyAssetToCache(scene.imageAsset)
        if (file == null) {
            Toast.makeText(this, R.string.scene_open_failed, Toast.LENGTH_SHORT).show()
            return
        }
        val i = Intent(this, MPVActivity::class.java)
        i.putExtra("filepath", file.absolutePath)
        i.putExtra("show_exit", true)
        startActivity(i)
    }

    /** Copies a bundled panorama into cache once so mpv can open it by path. */
    private fun copyAssetToCache(assetPath: String): File? {
        return try {
            val out = File(cacheDir, assetPath.substringAfterLast('/'))
            if (!out.exists() || out.length() == 0L) {
                assets.open(assetPath).use { input ->
                    out.outputStream().use { input.copyTo(it) }
                }
            }
            out
        } catch (_: Exception) {
            null
        }
    }
}
