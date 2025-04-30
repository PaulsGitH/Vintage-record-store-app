package ie.setu.album.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import ie.setu.album.R
import ie.setu.album.adapters.AlbumAdapter
import ie.setu.album.adapters.AlbumListener
import ie.setu.album.databinding.ActivityAlbumListBinding
import ie.setu.album.main.MainApp
import ie.setu.album.models.AlbumModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        lifecycleScope.launch {
            val albums = withContext(Dispatchers.IO) {
                (application as MainApp).albums.findAll()
            }
            binding.recyclerView.layoutManager = LinearLayoutManager(this@AlbumListActivity)
            binding.recyclerView.adapter = AlbumAdapter(albums, this@AlbumListActivity)
        }

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

            R.id.item_filter_genre -> {
                filterByGenre()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            when (it.resultCode) {
                Activity.RESULT_OK -> {
                    lifecycleScope.launch {
                        val updatedList = withContext(Dispatchers.IO) {
                            app.albums.findAll()
                        }
                        (binding.recyclerView.adapter as AlbumAdapter)
                            .updateList(updatedList)
                    }
                }
                Activity.RESULT_CANCELED -> {
                    Snackbar.make(binding.root, "Album Add Cancelled", Snackbar.LENGTH_LONG).show()
                }
            }
        }

    override fun onAlbumClick(album: AlbumModel) {
        val viewIntent = Intent(this, ViewAlbumActivity::class.java)
        viewIntent.putExtra("album_view", album)
        startActivity(viewIntent)
    }

    fun showAlbums(albums: List<AlbumModel>) {
        (binding.recyclerView.adapter as AlbumAdapter).updateList(albums)
    }

    private fun filterAlbums(query: String) {
        val filteredList = app.albums.searchAll(query)
        (binding.recyclerView.adapter as AlbumAdapter).updateList(filteredList)
    }

    private fun filterByGenre() {
        val genres = resources.getStringArray(R.array.album_genres)
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.DialogTheme))
        builder.setTitle("Choose Genre")
        builder.setItems(genres) { _, which ->
            val selectedGenre = genres[which]
            val filteredList = app.albums.findAll().filter { it.albumGenre == selectedGenre }
            (binding.recyclerView.adapter as AlbumAdapter).updateList(filteredList)
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
}
