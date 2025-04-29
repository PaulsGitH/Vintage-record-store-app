package ie.setu.album.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.setu.album.R
import ie.setu.album.databinding.CardAlbumBinding
import ie.setu.album.main.MainApp
import ie.setu.album.models.AlbumModel

class AlbumAdapter(
    albums: List<AlbumModel>,
    private val listener: AlbumListener
) : RecyclerView.Adapter<AlbumAdapter.MainHolder>() {

    private val Albums = albums.toMutableList()

    fun updateList(newList: List<AlbumModel>) {
        Albums.clear()
        Albums.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardAlbumBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val album = Albums[holder.adapterPosition]
        holder.bind(album, listener)
    }

    override fun getItemCount(): Int = Albums.size

    class MainHolder(private val binding: CardAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: AlbumModel, listener: AlbumListener) {
            binding.AlbumTitle.text     = album.albumName
            binding.ArtistName.text     = album.artist
            binding.GenreName.text      = album.albumGenre
            binding.AlbumPrice.text     = "€${"%.2f".format(album.cost)}"
            binding.albumCardRating.rating = album.rating.toFloat()
            Picasso.get()
                .load(album.albumImage)
                .resize(200, 200)
                .into(binding.imageIcon)

            binding.root.setOnClickListener { listener.onAlbumClick(album) }

            binding.favouriteIcon.setImageResource(
                if (album.isFavorite) R.drawable.ic_favorite
                else R.drawable.ic_favorite_border
            )
            binding.favouriteIcon.setOnClickListener {
                val newState = !album.isFavorite
                album.isFavorite = newState
                binding.favouriteIcon.setImageResource(
                    if (newState) R.drawable.ic_favorite
                    else R.drawable.ic_favorite_border
                )
                (binding.root.context.applicationContext as MainApp)
                    .albums
                    .update(album)
            }
        }
    }
}

