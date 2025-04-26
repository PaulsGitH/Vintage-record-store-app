package ie.setu.album.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ie.setu.Album.R
import ie.setu.Album.adapters.AlbumAdapter
import ie.setu.Album.adapters.AlbumListener
import ie.setu.Album.databinding.ActivityFavoritesBinding
import ie.setu.Album.main.MainApp
import ie.setu.Album.models.AlbumModel

class FavoritesActivity : AppCompatActivity(), AlbumListener {

    private lateinit var binding: ActivityFavoritesBinding
    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        app = application as MainApp

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.topAppBar.title = getString(R.string.menu_favorites)

        binding.bottomNavigation.selectedItemId = R.id.nav_favorites

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_albums -> {
                    startActivity(Intent(this, AlbumListActivity::class.java))
                    true
                }
                else -> false
            }
        }

        loadFavorites()
    }

    private fun loadFavorites() {
        val favoriteAlbums = app.albums.findFavorites()

        if (favoriteAlbums.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.noFavouritesMessage.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.noFavouritesMessage.visibility = View.GONE
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.recyclerView.adapter = AlbumAdapter(favoriteAlbums, this)
        }
    }

    override fun onAlbumClick(album: AlbumModel) {
        loadFavorites()
    }
}
