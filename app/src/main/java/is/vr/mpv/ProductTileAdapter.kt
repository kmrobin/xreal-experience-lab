package `is`.vr.mpv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/** Grid tile for a 3D product: emoji badge + title + subtitle. */
class ProductTileAdapter(
    private val items: List<ProductItem>,
    private val onClick: (ProductItem) -> Unit,
) : RecyclerView.Adapter<ProductTileAdapter.TileHolder>() {

    class TileHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emoji: TextView = view.findViewById(R.id.tileEmoji)
        val title: TextView = view.findViewById(R.id.tileTitle)
        val subtitle: TextView = view.findViewById(R.id.tileSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_tile, parent, false)
        return TileHolder(v)
    }

    override fun onBindViewHolder(holder: TileHolder, position: Int) {
        val item = items[position]
        holder.emoji.text = item.emoji
        holder.title.text = item.title
        holder.subtitle.text = item.subtitle
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
