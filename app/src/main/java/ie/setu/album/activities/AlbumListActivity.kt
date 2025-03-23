package ie.setu.album.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ie.setu.Album.R
import ie.setu.Album.activities.AlbumActivity
import ie.setu.Album.adapters.AlbumAdapter
import ie.setu.Album.adapters.AlbumListener
import ie.setu.Album.databinding.ActivityAlbumListBinding
import ie.setu.Album.main.MainApp
import ie.setu.Album.models.AlbumModel

class AlbumListActivity : AppCompatActivity(), AlbumListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityAlbumListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.topAppBar.title = title
        setSupportActionBar(binding.topAppBar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = AlbumAdapter(app.albums.findAll(), this)

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Ensures proper navigation
                    startActivity(intent)
                    true
                }
                R.id.nav_albums -> true
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavouritesActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterAlbums(newText.orEmpty())
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, AlbumActivity::class.java)
                getResult.launch(launcherIntent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                (binding.recyclerView.adapter)?.
                notifyItemRangeChanged(0,app.albums.findAll().size)
                binding.recyclerView.adapter?.notifyDataSetChanged() // necessary to refresh menu after album deletion
            }
            if (it.resultCode == Activity.RESULT_CANCELED) {
                Snackbar.make(binding.root, "Album Add Cancelled", Snackbar.LENGTH_LONG).show()
            }
        }

    override fun onAlbumClick(album: AlbumModel) {
        val launcherIntent = Intent(this, AlbumActivity::class.java)
        launcherIntent.putExtra("album_edit", album)
        getResult.launch(launcherIntent)
    }

    private fun filterAlbums(query: String) {
        val filteredList = app.albums.findAll().filter {
            it.albumName.contains(query, ignoreCase = true)
        }

        (binding.recyclerView.adapter as AlbumAdapter).updateList(filteredList)
    }



}
