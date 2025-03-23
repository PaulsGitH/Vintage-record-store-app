package ie.setu.album.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ie.setu.Album.R
import ie.setu.Album.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)

        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.title = getString(R.string.menu_favorites)
        setSupportActionBar(binding.topAppBar)

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
    }
}
