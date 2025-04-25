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
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    true
                }
                R.id.nav_albums -> true
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
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
                return true
            }

            R.id.item_sort_artist -> {
                showAlbums(app.albums.findAll().sortedBy { it.artist })
                return true
            }

            R.id.item_sort_genre -> {
                showAlbums(app.albums.findAll().sortedBy { it.albumGenre })
                return true
            }

            R.id.item_sort_price_high -> {
                val sorted = app.albums.findAll().sortedByDescending { it.cost }
                (binding.recyclerView.adapter as AlbumAdapter).updateList(sorted)
            }

            R.id.item_sort_price_low -> {
                val sorted = app.albums.findAll().sortedBy { it.cost }
                (binding.recyclerView.adapter as AlbumAdapter).updateList(sorted)
            }

            R.id.item_sort_rating_high -> {
                val sorted = app.albums.findAll().sortedByDescending { it.rating }
                (binding.recyclerView.adapter as AlbumAdapter).updateList(sorted)
                return true
            }
            R.id.item_sort_rating_low -> {
                val sorted = app.albums.findAll().sortedBy { it.rating }
                (binding.recyclerView.adapter as AlbumAdapter).updateList(sorted)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val updatedList = app.albums.findAll()
                (binding.recyclerView.adapter as AlbumAdapter).updateList(updatedList)
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

    fun showAlbums(albums: List<AlbumModel>) {
        (binding.recyclerView.adapter as AlbumAdapter).updateList(albums)
    }

    private fun filterAlbums(query: String) {
        val filteredList = app.albums.searchAll(query)
        (binding.recyclerView.adapter as AlbumAdapter).updateList(filteredList)
    }



}
