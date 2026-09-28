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
    val emoji: String,        // shown on the product tile
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
        SceneItem(
            id = "bedroom",
            title = "Modern Bedroom",
            subtitle = "Interior design walkthrough",
            imageAsset = "scenes/scene_bedroom.jpg",
            thumbAsset = "scenes/thumbs/scene_bedroom.jpg",
        ),
    )

    // Products load bundled .glb models from assets/models/ (served via the
    // WebViewAssetLoader). Remote https URLs also work. Add a .glb there and a
    // matching entry here to extend the catalog.
    val products = listOf(
        ProductItem(
            id = "iphone_17_pro",
            title = "iPhone 17 Pro",
            subtitle = "6.3\" flagship review",
            modelSrc = "models/iphone_17_pro.glb",
            emoji = "\uD83D\uDCF1",
        ),
        ProductItem(
            id = "iphone_duo",
            title = "iPhone Duo",
            subtitle = "Dual-screen concept",
            modelSrc = "models/iphone_duo.glb",
            emoji = "\uD83D\uDCF1",
        ),
        ProductItem(
            id = "airpods",
            title = "AirPods",
            subtitle = "Audio product review",
            modelSrc = "models/airpods.glb",
            emoji = "\uD83C\uDFA7",
        ),
        ProductItem(
            id = "soap_shoe",
            title = "Soap Shoe",
            subtitle = "Sonic Frontiers sneaker",
            modelSrc = "models/soap_shoe.glb",
            emoji = "\uD83D\uDC5F",
        ),
        ProductItem(
            id = "air_jordan_dior",
            title = "Air Jordan 1 Dior",
            subtitle = "Low-top sneaker review",
            modelSrc = "models/air_jordan_dior.glb",
            emoji = "\uD83D\uDC5F",
        ),
        ProductItem(
            id = "astronaut",
            title = "Astronaut",
            subtitle = "Sample 3D model",
            modelSrc = "https://modelviewer.dev/shared-assets/models/Astronaut.glb",
            emoji = "\uD83D\uDE80",
        ),
    )
}
