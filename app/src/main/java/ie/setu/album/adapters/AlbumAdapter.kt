package ie.setu.album.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.setu.album.R
import ie.setu.album.databinding.CardAlbumBinding
import ie.setu.album.main.MainApp
import ie.setu.album.models.AlbumModel

class AlbumAdapter constructor(private var Albums: List<AlbumModel>,
                                   private val listener: AlbumListener) :
    RecyclerView.Adapter<AlbumAdapter.MainHolder>() {

    fun updateList(newList: List<AlbumModel>) {
        Albums = newList
        notifyDataSetChanged() //allows refresh to display all albums as added
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardAlbumBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val Album = Albums[holder.adapterPosition]
        holder.bind(Album, listener)
    }

    override fun getItemCount(): Int = Albums.size

    class MainHolder(private val binding : CardAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(Album: AlbumModel, listener: AlbumListener) {
            binding.AlbumTitle.text = Album.albumName
            binding.ArtistName.text  = Album.artist
         // binding.AlbumDescription.text = Album.albumDescription
            binding.GenreName.text   = Album.albumGenre
            binding.AlbumPrice.text = "€${String.format("%.2f", Album.cost)}"
            Picasso.get().load(Album.albumImage).resize(200, 200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onAlbumClick(Album) }
            binding.albumCardRating.rating = Album.rating.toFloat()


            binding.favouriteIcon.setImageResource(
                if (Album.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )

            binding.favouriteIcon.setOnClickListener {
                val newState = !Album.isFavorite
                Album.isFavorite = newState
                binding.favouriteIcon.setImageResource(
                    if (newState) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                )
                (binding.root.context.applicationContext as MainApp)
                    .albums
                    .update(Album)
            }
        }
    }
}

