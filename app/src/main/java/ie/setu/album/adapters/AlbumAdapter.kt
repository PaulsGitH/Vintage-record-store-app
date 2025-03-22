package ie.setu.Album.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.setu.Album.databinding.CardAlbumBinding
import ie.setu.Album.models.AlbumModel

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
            binding.AlbumTitle.text = Album.title
            binding.AlbumDescription.text = Album.description
            Picasso.get().load(Album.image).resize(200,200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onAlbumClick(Album)}
        }
    }
}

