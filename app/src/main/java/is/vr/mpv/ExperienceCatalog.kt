package `is`.vr.mpv

/** A 360° panorama scene the user can step into and explore with head rotation. */
data class SceneItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageAsset: String,   // e.g. "scenes/scene_dc_plaza.jpg"
    val thumbAsset: String,   // e.g. "scenes/thumbs/scene_dc_plaza.jpg"
    val audioAsset: String = "audio/scene_ambient.mp3",
    val category: String = "scene",   // "scene" (reviewer) or "tourist"
)

/** A 3D product the user can inspect by orbiting the model. */
data class ProductItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val modelSrc: String,     // URL or file:///android_asset/models/<name>.glb
    val emoji: String,        // shown on the product tile
    val shopping: Boolean = false,   // also shown in the Shopping section
)

/**
 * Catalog of prebuilt experiences shown to each persona. Scenes are bundled in
 * assets/scenes/. Products load a .glb model (bundled under assets/models/ or a
 * remote URL); drop new .glb files there and add an entry here to extend it.
 */
object ExperienceCatalog {

    val scenes = listOf(
        SceneItem(
            id = "taj_mahal",
            title = "Taj Mahal",
            subtitle = "Agra \u00b7 UNESCO World Heritage",
            imageAsset = "scenes/scene_taj_mahal.png",
            thumbAsset = "scenes/thumbs/scene_taj_mahal.jpg",
            audioAsset = "audio/scene_taj_mahal.mp3",
            category = "tourist",
        ),
        SceneItem(
            id = "science_city",
            title = "Science City, Kolkata",
            subtitle = "Convention Centre \u00b7 360\u00b0 view",
            imageAsset = "scenes/scene_science_city.jpg",
            thumbAsset = "scenes/thumbs/scene_science_city.jpg",
            audioAsset = "audio/scene_science_city.mp3",
            category = "tourist",
        ),
        SceneItem(
            id = "kashmir_2",
            title = "Kashmir Highlands",
            subtitle = "Mountain landscape 360\u00b0 view",
            imageAsset = "scenes/scene_kashmir2.png",
            thumbAsset = "scenes/thumbs/scene_kashmir2.jpg",
            category = "tourist",
        ),
        SceneItem(
            id = "dc_plaza",
            title = "Washington DC Plaza",
            subtitle = "Open-air people & places",
            imageAsset = "scenes/scene_dc_plaza.jpg",
            thumbAsset = "scenes/thumbs/scene_dc_plaza.jpg",
            audioAsset = "audio/scene_dc_plaza.mp3",
            category = "tourist",
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
            audioAsset = "audio/scene_canal_lock.mp3",
            category = "tourist",
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
            id = "nike_shoe",
            title = "Nike Sneaker",
            subtitle = "Athletic shoe review",
            modelSrc = "models/nike_shoe.glb",
            emoji = "\uD83D\uDC5F",
            shopping = true,
        ),
        ProductItem(
            id = "air_jordan_dior",
            title = "Air Jordan 1 Dior",
            subtitle = "Low-top sneaker review",
            modelSrc = "models/air_jordan_dior.glb",
            emoji = "\uD83D\uDC5F",
            shopping = true,
        ),
        ProductItem(
            id = "puma_shoe",
            title = "Puma Purple Shoe",
            subtitle = "3D-scanned sneaker",
            modelSrc = "models/puma_shoe.glb",
            emoji = "\uD83D\uDC5F",
            shopping = true,
        ),
        ProductItem(
            id = "nike_shoe_box",
            title = "Nike Shoe Box",
            subtitle = "Packaging preview",
            modelSrc = "models/nike_shoe_box.glb",
            emoji = "\uD83D\uDCE6",
            shopping = true,
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
