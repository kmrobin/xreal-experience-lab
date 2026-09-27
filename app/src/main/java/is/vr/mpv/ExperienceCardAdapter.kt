package `is`.vr.mpv

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/** Simple card: a thumbnail (decoded from assets) with a title and subtitle. */
data class CardEntry(
    val title: String,
    val subtitle: String,
    val thumbAsset: String?,
)

class ExperienceCardAdapter(
    private val context: Context,
    private val items: List<CardEntry>,
    private val onClick: (Int) -> Unit,
) : RecyclerView.Adapter<ExperienceCardAdapter.CardHolder>() {

    class CardHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.cardImage)
        val title: TextView = view.findViewById(R.id.cardTitle)
        val subtitle: TextView = view.findViewById(R.id.cardSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_experience_card, parent, false)
        return CardHolder(v)
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.image.setImageBitmap(loadAsset(item.thumbAsset))
        holder.itemView.setOnClickListener { onClick(holder.bindingAdapterPosition) }
    }

    override fun getItemCount() = items.size

    private fun loadAsset(path: String?) = path?.let {
        try {
            context.assets.open(it).use { s -> BitmapFactory.decodeStream(s) }
        } catch (_: Exception) {
            null
        }
    }
}
