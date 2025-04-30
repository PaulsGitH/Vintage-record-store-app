package ie.setu.album.activities

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import ie.setu.album.R
import ie.setu.album.adapters.AlbumAdapter
import ie.setu.album.adapters.AlbumListener
import ie.setu.album.databinding.ActivityFavoritesBinding
import ie.setu.album.main.MainApp
import ie.setu.album.models.AlbumModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        val typeface = ResourcesCompat.getFont(this, R.font.vintage)
        val toolbarTitle = findViewById<MaterialToolbar>(R.id.topAppBar)

        for (i in 0 until toolbarTitle.childCount) {
            val view = toolbarTitle.getChildAt(i)
            if (view is TextView && view.text == toolbarTitle.title) {
                view.typeface = typeface
                view.setTextColor(ContextCompat.getColor(this, R.color.hot_pink))
                view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            }
        }

    }

    private fun loadFavorites() {
        // 1) launch a coroutine tied to this Activity’s lifecycle
        lifecycleScope.launch {
            // 2) do your blocking Firebase call off the main thread
            val favouriteAlbums = withContext(Dispatchers.IO) {
                app.albums.findFavorites()
            }

            // 3) back on Main, update the UI
            if (favouriteAlbums.isEmpty()) {
                binding.recyclerView.visibility       = View.GONE
                binding.noFavouritesMessage.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility       = View.VISIBLE
                binding.noFavouritesMessage.visibility = View.GONE
                binding.recyclerView.layoutManager     = LinearLayoutManager(this@FavoritesActivity)
                binding.recyclerView.adapter           =
                    AlbumAdapter(favouriteAlbums, this@FavoritesActivity)
            }
        }
    }

    override fun onAlbumClick(album: AlbumModel) {
        val viewIntent = Intent(this, ViewAlbumActivity::class.java)
        viewIntent.putExtra("album_view", album)
        startActivity(viewIntent)
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }
}

