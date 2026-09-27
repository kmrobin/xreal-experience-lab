package `is`.vr.mpv

/** A 360° panorama scene the user can step into and explore with head rotation. */
data class SceneItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageAsset: String,   // e.g. "scenes/scene_dc_plaza.jpg"
    val thumbAsset: String,   // e.g. "scenes/thumbs/scene_dc_plaza.jpg"
)

/** A 3D product the user can inspect by orbiting the model. */
data class ProductItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val modelSrc: String,     // URL or file:///android_asset/models/<name>.glb
)

/**
 * Catalog of prebuilt experiences shown to each persona. Scenes are bundled in
 * assets/scenes/. Products load a .glb model (bundled under assets/models/ or a
 * remote URL); drop new .glb files there and add an entry here to extend it.
 */
object ExperienceCatalog {

    val scenes = listOf(
        SceneItem(
            id = "dc_plaza",
            title = "Washington DC Plaza",
            subtitle = "Open-air people & places",
            imageAsset = "scenes/scene_dc_plaza.jpg",
            thumbAsset = "scenes/thumbs/scene_dc_plaza.jpg",
        ),
        SceneItem(
            id = "living_room",
            title = "Modern Living Room",
            subtitle = "Interior design walkthrough",
            imageAsset = "scenes/scene_living_room.jpg",
            thumbAsset = "scenes/thumbs/scene_living_room.jpg",
        ),
        SceneItem(
            id = "canal_lock",
            title = "Waltrop Canal Lock",
            subtitle = "Historic Alte Schachtschleuse",
            imageAsset = "scenes/scene_canal_lock.jpg",
            thumbAsset = "scenes/thumbs/scene_canal_lock.jpg",
        ),
    )

    // Products load bundled .glb models from assets/models/ (served via the
    // WebViewAssetLoader). Remote https URLs also work. Add a .glb there and a
    // matching entry here to extend the catalog.
    val products = listOf(
        ProductItem(
            id = "iphone_17_pro",
            title = "iPhone 17 Pro",
            subtitle = "6.3\" flagship · orbit to inspect",
            modelSrc = "models/iphone_17_pro.glb",
        ),
        ProductItem(
            id = "iphone_duo",
            title = "iPhone Duo",
            subtitle = "Dual-screen concept · review the design",
            modelSrc = "models/iphone_duo.glb",
        ),
        ProductItem(
            id = "astronaut",
            title = "Astronaut",
            subtitle = "Sample model · streamed online",
            modelSrc = "https://modelviewer.dev/shared-assets/models/Astronaut.glb",
        ),
    )
}
